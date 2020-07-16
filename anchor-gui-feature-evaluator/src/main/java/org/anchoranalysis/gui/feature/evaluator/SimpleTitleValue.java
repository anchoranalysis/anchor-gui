/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.gui.cfgnrgtable.TitleValueTableModel.ITitleValueRow;

class SimpleTitleValue implements ITitleValueRow {

    private String title;
    private String value;

    public SimpleTitleValue(String title, String value) {
        super();
        this.title = title;
        this.value = value;
    }

    @Override
    public String genTitle() {
        return title;
    }

    @Override
    public String genValue(CfgNRGInstantState state) {
        return value;
    }
}
