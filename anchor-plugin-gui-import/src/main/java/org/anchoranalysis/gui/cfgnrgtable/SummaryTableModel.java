/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;

public class SummaryTableModel extends TitleValueTableModel implements IUpdateTableData {

    private static final long serialVersionUID = 7092606148170077373L;

    public SummaryTableModel() {
        addEntry(
                new ITitleValueRow() {

                    @Override
                    public String genValue(CfgNRGInstantState state) {
                        return Integer.toString(state.getIndex());
                    }

                    @Override
                    public String genTitle() {
                        return "Iteration";
                    }
                });
        addEntry(
                new ITitleValueRow() {

                    @Override
                    public String genValue(CfgNRGInstantState state) {
                        if (state.getCfgNRG() != null) {
                            return Integer.toString(state.getCfgNRG().getCfg().size());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String genTitle() {
                        return "Size";
                    }
                });
        addEntry(
                new ITitleValueRow() {

                    @Override
                    public String genValue(CfgNRGInstantState state) {
                        if (state.getCfgNRG() != null) {
                            return String.format("%f", state.getCfgNRG().getNrgTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String genTitle() {
                        return "NRG Total";
                    }
                });
        addEntry(
                new ITitleValueRow() {

                    @Override
                    public String genValue(CfgNRGInstantState state) {
                        if (state.getCfgNRG() != null) {
                            return String.format(
                                    "%f", state.getCfgNRG().getCalcMarkInd().getNrgTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String genTitle() {
                        return "NRG Total - Individual";
                    }
                });
        addEntry(
                new ITitleValueRow() {

                    @Override
                    public String genValue(CfgNRGInstantState state) {
                        if (state.getCfgNRG() != null) {
                            return String.format(
                                    "%f", state.getCfgNRG().getCalcMarkPair().getNRGTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String genTitle() {
                        return "NRG Total - Pairs";
                    }
                });
        addEntry(
                new ITitleValueRow() {

                    @Override
                    public String genValue(CfgNRGInstantState state) {
                        if (state.getCfgNRG() != null) {
                            return String.format(
                                    "%f", state.getCfgNRG().getCalcMarkAll().getNRGTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String genTitle() {
                        return "NRG Total - All";
                    }
                });
    }
}
