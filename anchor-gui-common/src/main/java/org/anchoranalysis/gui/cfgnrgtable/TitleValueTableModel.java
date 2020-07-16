/*-
 * #%L
 * anchor-gui-common
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
