/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SummaryTablePanel {

    private TablePanel tablePanel;
    private SummaryTableModel tableModel;

    public SummaryTablePanel() {

        this.tableModel = new SummaryTableModel();
        this.tablePanel = new TablePanel("Summary", tableModel, false);
        this.tablePanel
                .getTable()
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(new AlignRenderer(SwingConstants.RIGHT));
    }

    public JPanel getPanel() {
        return tablePanel.getPanel();
    }

    public IUpdateTableData getUpdateTableData() {
        return tableModel;
    }
}
