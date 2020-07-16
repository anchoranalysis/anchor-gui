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

package org.anchoranalysis.gui.cfgnrgtable;

import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.anchoranalysis.anchor.mpp.feature.nrg.NRGPair;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.gui.videostats.ISelectIndicesSendable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PairTablePanel {

    private TablePanel tablePanel;
    private PairTableModel tableModel;
    private SelectionUpdater selectionUpdater;

    private IndicesSelection selectionIndices;

    private static Log log = LogFactory.getLog(PairTablePanel.class);

    private class SelectionUpdater implements ListSelectionListener {

        private boolean enabled = true;

        @Override
        public void valueChanged(ListSelectionEvent e) {

            log.debug("Pair valueChanged");

            if (enabled) {

                // This can be hit many times after the initial change, as a cycle of selectMarkOnly
                // kick in
                //   but the selectionIndices should prevent any global changes from filtering
                // through

                int[] selectedIDs = calcSelectedIDs();
                selectionIndices.setCurrentSelection(selectedIDs);
            }
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public PairTablePanel(ColorIndex colorIndex, IndicesSelection lastExplicitSelection) {

        selectionIndices = new IndicesSelection(lastExplicitSelection.getCurrentSelection());

        this.tableModel = new PairTableModel(colorIndex);

        this.tablePanel = new TablePanel("Pairs", tableModel, true);

        this.tablePanel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // We set the column widths
        int col = 0;
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(col++)
                .setPreferredWidth(TableColumnConstants.WIDTH_COLOR);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(col++)
                .setPreferredWidth(TableColumnConstants.WIDTH_COLOR);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(col++)
                .setPreferredWidth(TableColumnConstants.WIDTH_ID);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(col++)
                .setPreferredWidth(TableColumnConstants.WIDTH_ID);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(col++)
                .setPreferredWidth(TableColumnConstants.WIDTH_NRG);

        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(new ColorRenderer());
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(new ColorRenderer());
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(4)
                .setCellRenderer(new AlignRenderer(SwingConstants.RIGHT));

        // this.tablePanel.getTable().getColumnModel().getColumn(4).setHorizontalAlignment(SwingConstants.RIGHT);

        this.selectionUpdater = new SelectionUpdater();

        this.tablePanel.getTable().getSelectionModel().addListSelectionListener(selectionUpdater);
    }

    public JPanel getPanel() {
        return tablePanel.getPanel();
    }

    public IUpdateTableData getUpdateTableData() {
        return tableModel;
    }

    public int[] getSelectedIDs() {
        return selectionIndices.getCurrentSelection();
    }

    public int[] calcSelectedIDs() {

        HashSet<Integer> idSet = new HashSet<>();

        int i = 0;
        for (NRGPair pair : tableModel.getPairs()) {

            if (this.tablePanel.getTable().getSelectionModel().isSelectedIndex(i++)) {
                idSet.add(pair.getPair().getSource().getId());
                idSet.add(pair.getPair().getDestination().getId());
            }
        }

        int[] outArr = new int[idSet.size()];
        i = 0;
        for (int id : idSet) {
            outArr[i++] = id;
        }

        return outArr;
    }

    public ISelectIndicesSendable getSelectMarksSendable() {
        return new ISelectIndicesSendable() {

            @Override
            public void selectIndicesOnly(int[] ids) {

                selectionIndices.setCurrentSelection(ids);

                Set<Integer> idSet = IDUtilities.setFromIntArr(ids);

                // We disable the selection updater, so no update occurs after set the selection
                //   as it might only be a subset of the overall ids, which would then change
                //   our current selection vlaue
                selectionUpdater.setEnabled(false);

                tablePanel.getTable().getSelectionModel().clearSelection();

                // We loop through every set of pairs, and select if both are in our hashset
                for (int i = 0; i < tableModel.getPairs().length; i++) {

                    NRGPair pair = tableModel.getPairs()[i];

                    if (idSet.contains(pair.getPair().getSource().getId())
                            && idSet.contains(pair.getPair().getDestination().getId())) {
                        tablePanel.getTable().getSelectionModel().addSelectionInterval(i, i);
                        IDUtilities.scrollJTableToRow(tablePanel.getTable(), i);
                    }
                }

                selectionUpdater.setEnabled(true);
            }
        };
    }

    public void addMouseListener(MouseListener l) {
        tablePanel.addMouseListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        tablePanel.removeMouseListener(l);
    }
}
