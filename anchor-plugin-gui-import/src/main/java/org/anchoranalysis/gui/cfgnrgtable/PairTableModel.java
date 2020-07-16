/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import javax.swing.table.AbstractTableModel;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.NRGPair;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedPairs;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;

public class PairTableModel extends AbstractTableModel implements IUpdateTableData {

    private static final long serialVersionUID = 7882539188914833799L;

    private NRGSavedPairs nrgSavedPairs;

    private ColorIndex colorIndex;

    private NRGPair[] pairArr;

    public PairTableModel(ColorIndex colorIndex) {
        super();
        this.colorIndex = colorIndex;
    }

    @Override
    public void updateTableData(CfgNRGInstantState state) {

        if (state.getCfgNRG() != null) {
            this.nrgSavedPairs = state.getCfgNRG().getCalcMarkPair();
            this.pairArr = this.nrgSavedPairs.createPairsUnique().toArray(new NRGPair[] {});
        } else {
            this.pairArr = new NRGPair[] {};
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
                return "nrg";
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
                return String.format("%16.3f", pairArr[row].getNRG().getTotal());
            default:
                return "error";
        }
    }

    public NRGPair[] getPairs() {
        return pairArr;
    }
}
