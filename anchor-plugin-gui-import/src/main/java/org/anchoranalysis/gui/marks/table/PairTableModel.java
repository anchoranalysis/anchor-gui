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

import javax.swing.table.AbstractTableModel;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.mpp.feature.energy.EnergyPair;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.feature.energy.saved.EnergySavedPairs;

@RequiredArgsConstructor
public class PairTableModel extends AbstractTableModel implements IUpdateTableData {

    private static final long serialVersionUID = 7882539188914833799L;

    // START REQUIRED ARGUMENTS
    private final ColorIndex colorIndex;
    // END REQUIRED ARGUMENTS

    private EnergySavedPairs energySavedPairs;
    private EnergyPair[] pairArr;

    @Override
    public void updateTableData(IndexableMarksWithEnergy state) {

        if (state.getMarks() != null) {
            this.energySavedPairs = state.getMarks().getPair();
            this.pairArr = this.energySavedPairs.createPairsUnique().toArray(new EnergyPair[] {});
        } else {
            this.pairArr = new EnergyPair[] {};
        }
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int col) {

        switch (col) {
            case 0:
                return "";
            case 1:
                return "";
            case 2:
                return "id-s";
            case 3:
                return "id-d";
            case 4:
                return "energy";
            default:
                assert false;
                return "error";
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class getColumnClass(int c) {

        switch (c) {
            case 0:
                return RGBColor.class;
            case 1:
                return RGBColor.class;
            case 2:
                return Integer.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
            default:
                throw new AnchorImpossibleSituationException();
        }
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getRowCount() {
        return this.pairArr.length;
    }

    @Override
    public Object getValueAt(int row, int column) {

        if (row >= getRowCount()) {
            return "error";
        }

        switch (column) {
            case 0:
                return colorIndex.get(pairArr[row].getPair().getSource().getId());
            case 1:
                return colorIndex.get(pairArr[row].getPair().getDestination().getId());
            case 2:
                return this.pairArr[row].getPair().getSource().getId();
            case 3:
                return this.pairArr[row].getPair().getDestination().getId();
            case 4:
                return String.format("%16.3f", pairArr[row].getEnergyTotal().getTotal());
            default:
                return "error";
        }
    }

    public EnergyPair[] getPairs() {
        return pairArr;
    }
}
