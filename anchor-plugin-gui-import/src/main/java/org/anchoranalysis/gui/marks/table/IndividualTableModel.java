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
import org.anchoranalysis.anchor.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.anchor.mpp.feature.energy.saved.EnergySavedInd;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;

public class IndividualTableModel extends AbstractTableModel implements IUpdateTableData {

    private static final long serialVersionUID = 7882539188914833799L;

    // START BEAN PROPERTIES
    private MarkCollection cfg;

    private EnergySavedInd energySavedInd;
    // END BEAN PROPERTIES

    private final ColorIndex colorIndex;

    public IndividualTableModel(ColorIndex colorIndex) {
        super();
        this.colorIndex = colorIndex;
    }

    @Override
    public void updateTableData(IndexableMarksWithEnergy state) {

        if (state.getMarks() != null) {
            this.cfg = state.getMarks().getMarks();
            this.energySavedInd = state.getMarks().getIndividual();
        } else {
            this.cfg = new MarkCollection();
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

    public MarkCollection getCfg() {
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
                // Energy
                return String.format("%16.3f", energySavedInd.get(row).getTotal());
            default:
                return "error";
        }
    }
}
