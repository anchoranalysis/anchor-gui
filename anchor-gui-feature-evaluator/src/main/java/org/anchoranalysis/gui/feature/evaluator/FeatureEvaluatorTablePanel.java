/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator;

import java.awt.BorderLayout;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.cfgnrg.StatePanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;

public class FeatureEvaluatorTablePanel extends StatePanel<OverlayCollectionWithImgStack> {

    private JPanel delegate;

    private SinglePairUpdater updater;

    public FeatureEvaluatorTablePanel(
            FeatureListSrc featureListSrc, boolean defaultKeepLastValid, Logger logger) {
        super();

        OverlayDescriptionPanel overlayDescriptionPanel = new OverlayDescriptionPanel();

        UpdatableTreeTable updatableModel =
                UpdatableTreeTableFactory.create(overlayDescriptionPanel, featureListSrc, logger);

        delegate =
                createBigPanel(
                        overlayDescriptionPanel.getPanel(),
                        overlayDescriptionPanel.createSummaryPanel(),
                        updatableModel);

        updater = updatableModel.getUpdater();
    }

    /**
     * Creates a big panel with all our components: TOP (North): a descriptionPanel MIDDLE-TOP
     * (CENTER): a component (first component in half-filled splitPane) MIDDLE-BOTTOM (CENTER): a
     * component (second component in half-filled splitPane)
     *
     * @param overlayDescriptionPanel
     * @return
     */
    private static JPanel createBigPanel(
            JComponent componentTop,
            JComponent componentCenter1,
            UpdatableTreeTable componentCenter2) {

        JPanel panel = new JPanel();

        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        panel.setLayout(new BorderLayout());

        // TOP
        panel.add(componentTop, BorderLayout.NORTH);

        // MIDDLE-TOP
        JSplitPane splitPane = wrapInSplitPane(componentCenter1);
        panel.add(wrapInScrollPanel(splitPane), BorderLayout.CENTER);

        // MIDDLE-BOTTOM
        componentCenter2.addToSplitPane(splitPane);

        return panel;
    }

    // Wraps in a SplitPane but only adds one item, the other must be added later
    private static JSplitPane wrapInSplitPane(JComponent content) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.add(content);
        return splitPane;
    }

    private static JPanel wrapInScrollPanel(JComponent content) {
        JPanel panelScroll = new JPanel();
        panelScroll.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        panelScroll.setLayout(new BorderLayout());
        panelScroll.add(content, BorderLayout.CENTER);
        return panelScroll;
    }

    @Override
    public JPanel getPanel() {
        return delegate;
    }

    @Override
    public void updateState(OverlayCollectionWithImgStack state) throws StatePanelUpdateException {

        try {
            updater.updateModel(state);
        } catch (CreateException e) {
            throw new StatePanelUpdateException(e);
        }
    }

    @Override
    public Optional<IPropertyValueSendable<IntArray>> getSelectMarksSendable() {
        return Optional.empty();
    }

    @Override
    public Optional<IPropertyValueReceivable<IntArray>> getSelectMarksReceivable() {
        return Optional.empty();
    }

    @Override
    public Optional<IPropertyValueReceivable<OverlayCollection>>
            getSelectOverlayCollectionReceivable() {
        return Optional.empty();
    }

    @Override
    public Optional<IPropertyValueReceivable<Integer>> getSelectIndexReceivable() {
        return Optional.empty();
    }
}
