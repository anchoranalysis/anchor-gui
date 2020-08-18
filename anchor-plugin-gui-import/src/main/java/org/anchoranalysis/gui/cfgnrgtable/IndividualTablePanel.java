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
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.gui.videostats.ISelectIndicesSendable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IndividualTablePanel {

    private TablePanel tablePanel;
    private IndividualTableModel tableModel;

    private IndicesSelection selectionIndices;

    private SelectionUpdater selectionUpdater;

    private static Log log = LogFactory.getLog(IndividualTablePanel.class);

    private class SelectionUpdater implements ListSelectionListener {

        private boolean enabled = true;

        @Override
        public void valueChanged(ListSelectionEvent e) {

            log.debug("Individual valueChanged");

            if (enabled) {

                // This can be hit many times after the initial change, as a cycle of selectMarkOnly
                // kick in
                //   but the selectionIndices should prevent any global changes from filtering
                // through
                int[] selectedIDs = calculateSelectedIdentifiers();
                selectionIndices.setCurrentSelection(selectedIDs);
            }
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public IndividualTablePanel(ColorIndex colorIndex, IndicesSelection lastExplicitSelection) {

        this.selectionIndices = new IndicesSelection(lastExplicitSelection.getCurrentSelection());

        this.tableModel = new IndividualTableModel(colorIndex);

        this.tablePanel = new TablePanel("Individual", tableModel, true);

        this.tablePanel.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // We set the column widths
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(TableColumnConstants.WIDTH_COLOR);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(TableColumnConstants.WIDTH_ID);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(TableColumnConstants.WIDTH_NRG);

        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(0)
                .setCellRenderer(new ColorRenderer());
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(new AlignRenderer(SwingConstants.RIGHT));

        this.selectionUpdater = new SelectionUpdater();
        this.tablePanel
                .getTable()
                .getSelectionModel()
                .addListSelectionListener(this.selectionUpdater);
    }

    private int[] calculateSelectedIdentifiers() {
        int[] selectedIndices = this.tablePanel.getTable().getSelectedRows();

        int markIDs[] = new int[selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            Mark m = this.tableModel.getMark(selectedIndices[i]);
            markIDs[i] = m.getId();
        }

        return markIDs;
    }

    public int[] getSelectedIDs() {
        return selectionIndices.getCurrentSelection();
    }

    public JPanel getPanel() {
        return tablePanel.getPanel();
    }

    public IUpdateTableData getUpdateTableData() {
        return tableModel;
    }

    public ISelectIndicesSendable getSelectMarksSendable() {
        return ids -> {
            // If the ids are the same as our current selection, we don't need to change
            // anything
            selectionIndices.setCurrentSelection(ids);
            //	return;
            // }

            Set<Integer> idSet = IDUtilities.setFromIntArr(ids);

            selectionUpdater.setEnabled(false);

            tablePanel.getTable().getSelectionModel().clearSelection();

            int i = 0;
            for (Mark mark : tableModel.getCfg()) {

                if (idSet.contains(mark.getId())) {
                    tablePanel.getTable().getSelectionModel().addSelectionInterval(i, i);
                    IDUtilities.scrollJTableToRow(tablePanel.getTable(), i);
                }
                i++;
            }

            selectionUpdater.setEnabled(true);
        };
    }

    public void addMouseListener(MouseListener listener) {
        tablePanel.addMouseListener(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        tablePanel.removeMouseListener(listener);
    }
}
