/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.marks.table;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.gui.videostats.link.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.PropertyValueReceivableFromIndicesSelection;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.image.OverlaysWithEnergyStack;
import org.anchoranalysis.gui.indices.DualIndicesSelection;
import org.anchoranalysis.gui.marks.MarkCollectionUtilities;
import org.anchoranalysis.gui.marks.StatePanel;
import org.anchoranalysis.gui.marks.StatePanelUpdateException;
import org.anchoranalysis.gui.property.PropertyValueChangeEvent;
import org.anchoranalysis.gui.property.PropertyValueChangeListener;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.collection.OverlayCollection;

public class MarksEnergyTablePanel extends StatePanel<IndexableMarksWithEnergy> {

    private JPanel panelBig;

    private ArrayList<IUpdateTableData> updateList = new ArrayList<>();
    private PairTablePanel pairPanel;
    private IndividualTablePanel individualPanel;

    private DualIndicesSelection selectionIndices = new DualIndicesSelection();

    private IndexableMarksWithEnergy state;

    private EnergyStack associatedRaster;

    private PropertyValueChangeListenerList<OverlayCollection> eventListenerListOverlayCollection =
            new PropertyValueChangeListenerList<>();
    private PropertyValueChangeListenerList<OverlaysWithEnergyStack>
            eventListenerListOverlayCollectionWithStack = new PropertyValueChangeListenerList<>();

    private class ClickAdapterIndividual extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent arg0) {
            super.mouseReleased(arg0);

            // This event always seems to happen after any ListSelection events, so we rely
            //   on it to occur, on each occasion we want to send a ObjChangeEvent for clicking
            // on a mark

            selectionIndices.updateBoth(individualPanel.getSelectedIDs());
            pairPanel.getSelectMarksSendable().selectIndicesOnly(individualPanel.getSelectedIDs());

            // Crucially the selectionIndices must be updated with the ListSelection change, before
            // it occurs, which we assume happens
            triggerEvent();
        }
    }

    private class ClickAdapterPair extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent arg0) {
            super.mouseReleased(arg0);

            // This event always seems to happen after any ListSelection events, so we rely
            //   on it to occur, on each occasion we want to send a ObjChangeEvent for clicking
            // on a mark

            selectionIndices.updateBoth(pairPanel.getSelectedIDs());
            individualPanel.getSelectMarksSendable().selectIndicesOnly(pairPanel.getSelectedIDs());

            // Crucially the selectionIndices must be updated with the ListSelection change, before
            // it occurs
            //  we assume this happens
            triggerEvent();
        }
    }

    public MarksEnergyTablePanel(ColorIndex colorIndex, EnergyStack associatedRaster) {
        super();

        this.associatedRaster = associatedRaster;

        final SummaryTablePanel summaryPanel = new SummaryTablePanel();
        individualPanel =
                new IndividualTablePanel(colorIndex, selectionIndices.getCurrentSelection());
        pairPanel = new PairTablePanel(colorIndex, selectionIndices.getCurrentSelection());

        ClickAdapterIndividual ca = new ClickAdapterIndividual();
        individualPanel.addMouseListener(ca);
        pairPanel.addMouseListener(new ClickAdapterPair());

        this.panelBig = new JPanel();

        panelBig.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        panelBig.setLayout(new BorderLayout());

        panelBig.add(summaryPanel.getPanel(), BorderLayout.NORTH);

        JPanel panelScroll = new JPanel();
        panelScroll.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        panelScroll.setLayout(new GridLayout(2, 1));
        panelScroll.add(individualPanel.getPanel());
        panelScroll.add(pairPanel.getPanel());

        panelBig.add(panelScroll, BorderLayout.CENTER);

        updateList.add(summaryPanel.getUpdateTableData());
        updateList.add(individualPanel.getUpdateTableData());
        updateList.add(pairPanel.getUpdateTableData());
    }

    private void triggerEvent() {
        MarkCollection marks = state.getMarks() != null ? state.getMarks().getMarks() : null;
        if (marks != null) {
            MarkCollection marksSubset =
                    MarkCollectionUtilities.subsetMarks(
                            marks, selectionIndices.getCurrentSelection());
            RegionMembershipWithFlags regionMembership =
                    RegionMapSingleton.instance()
                            .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);
            OverlayCollection overlaySubset =
                    OverlayCollectionMarkFactory.createWithoutColor(marksSubset, regionMembership);
            triggerObjectChangeEvent(overlaySubset);
        }
    }

    @Override
    public JPanel getPanel() {
        return panelBig;
    }

    @Override
    public void updateState(IndexableMarksWithEnergy state) throws StatePanelUpdateException {

        this.state = state;

        // We do this before updateTableData as it will change the selection
        for (IUpdateTableData updater : updateList) {
            updater.updateTableData(state);
        }

        individualPanel
                .getSelectMarksSendable()
                .selectIndicesOnly(selectionIndices.getCurrentSelection().getCurrentSelection());
        pairPanel
                .getSelectMarksSendable()
                .selectIndicesOnly(selectionIndices.getCurrentSelection().getCurrentSelection());
    }

    private void triggerObjectChangeEvent(OverlayCollection state) {
        for (PropertyValueChangeListener<OverlayCollection> l :
                eventListenerListOverlayCollection) {
            l.propertyValueChanged(new PropertyValueChangeEvent<>(this, state, false));
        }

        for (PropertyValueChangeListener<OverlaysWithEnergyStack> l :
                eventListenerListOverlayCollectionWithStack) {
            l.propertyValueChanged(
                    new PropertyValueChangeEvent<>(
                            this, new OverlaysWithEnergyStack(state, associatedRaster), false));
        }
    }

    @Override
    public Optional<IPropertyValueSendable<IntArray>> getSelectMarksSendable() {
        return Optional.of(
                (IntArray value, boolean adjusting) -> {
                    int[] ids = value.getArr();
                    selectionIndices.setCurrentSelection(ids);

                    individualPanel.getSelectMarksSendable().selectIndicesOnly(ids);
                    pairPanel.getSelectMarksSendable().selectIndicesOnly(ids);
                });
    }

    @Override
    public Optional<IPropertyValueReceivable<OverlayCollection>>
            getSelectOverlayCollectionReceivable() {
        return Optional.of(eventListenerListOverlayCollection.createPropertyValueReceivable());
    }

    @Override
    public Optional<IPropertyValueReceivable<IntArray>> getSelectMarksReceivable() {
        return Optional.of(
                new PropertyValueReceivableFromIndicesSelection(
                        selectionIndices.getLastExplicitSelection()));
    }

    @Override
    public Optional<IPropertyValueReceivable<Integer>> getSelectIndexReceivable() {
        return Optional.empty();
    }
}
