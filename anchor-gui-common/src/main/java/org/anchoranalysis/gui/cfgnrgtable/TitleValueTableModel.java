/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;

public class TitleValueTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 4158592575997677838L;

    private CfgNRGInstantState crnt;

    private ArrayList<ITitleValueRow> entryList = new ArrayList<>();

    public static interface ITitleValueRow {
        String genTitle();

        String genValue(CfgNRGInstantState state);
    }

    // Constructor
    public TitleValueTableModel() {
        super();
        entryList = new ArrayList<>();
    }

    public void updateTableData(CfgNRGInstantState state) {
        this.crnt = state;
        fireTableDataChanged();
    }

    public void addEntry(ITitleValueRow entry) {
        this.entryList.add(entry);
    }

    public void clear() {
        this.entryList.clear();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return this.entryList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

        switch (col) {
            case 0:
                return getTitle(row);
            case 1:
                return getValue(row);
            default:
                assert false;
                return "error";
        }
    }

    private String getTitle(int index) {

        if (index >= this.entryList.size()) {
            // assert false;
            return "";
        }

        return this.entryList.get(index).genTitle();
    }

    private String getValue(int index) {

        if (index >= this.entryList.size()) {
            return "";
        }

        return this.entryList.get(index).genValue(crnt);
    }
}
