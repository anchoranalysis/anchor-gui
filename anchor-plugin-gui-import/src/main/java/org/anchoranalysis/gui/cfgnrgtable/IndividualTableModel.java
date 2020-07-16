/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import javax.swing.table.AbstractTableModel;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.saved.NRGSavedInd;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;

public class IndividualTableModel extends AbstractTableModel implements IUpdateTableData {

    private static final long serialVersionUID = 7882539188914833799L;

    private Cfg cfg;

    private NRGSavedInd nrgSavedInd;

    private ColorIndex colorIndex;

    public IndividualTableModel(ColorIndex colorIndex) {
        super();
        this.colorIndex = colorIndex;
    }

    @Override
    public void updateTableData(CfgNRGInstantState state) {

        if (state.getCfgNRG() != null) {
            this.cfg = state.getCfgNRG().getCfg();
            this.nrgSavedInd = state.getCfgNRG().getCalcMarkInd();
        } else {
            this.cfg = new Cfg();
        }
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int col) {

        switch (col) {
            case 0:
                return "";
            case 1:
                return "id";
            case 2:
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
                return Integer.class;
            case 2:
                return String.class;
            default:
                throw new AnchorImpossibleSituationException();
        }
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return this.cfg.size();
    }

    public Mark getMark(int index) {
        return cfg.get(index);
    }

    public Cfg getCfg() {
        return cfg;
    }

    @Override
    public Object getValueAt(int row, int column) {

        if (row >= cfg.size()) {
            return "error";
        }

        switch (column) {
            case 0:
                // ICON
                return colorIndex.get(cfg.get(row).getId());
            case 1:
                // ID
                return cfg.get(row).getId();
            case 2:
                // NRG
                return String.format("%16.3f", nrgSavedInd.get(row).getTotal());
            default:
                return "error";
        }
    }
}
