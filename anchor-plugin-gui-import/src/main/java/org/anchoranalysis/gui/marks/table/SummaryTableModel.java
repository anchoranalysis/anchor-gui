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

import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;

public class SummaryTableModel extends TitleValueTableModel implements IUpdateTableData {

    private static final long serialVersionUID = 7092606148170077373L;

    public SummaryTableModel() {
        addEntry(
                new TitleValueRow() {

                    @Override
                    public String value(IndexableMarksWithEnergy state) {
                        return Integer.toString(state.getIndex());
                    }

                    @Override
                    public String title() {
                        return "Iteration";
                    }
                });
        addEntry(
                new TitleValueRow() {

                    @Override
                    public String value(IndexableMarksWithEnergy state) {
                        if (state.getMarks() != null) {
                            return Integer.toString(state.getMarks().size());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String title() {
                        return "Size";
                    }
                });
        addEntry(
                new TitleValueRow() {

                    @Override
                    public String value(IndexableMarksWithEnergy state) {
                        if (state.getMarks() != null) {
                            return String.format("%f", state.getMarks().getEnergyTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String title() {
                        return "Energy Total";
                    }
                });
        addEntry(
                new TitleValueRow() {

                    @Override
                    public String value(IndexableMarksWithEnergy state) {
                        if (state.getMarks() != null) {
                            return String.format(
                                    "%f", state.getMarks().getIndividual().getEnergyTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String title() {
                        return "Energy Total - Individual";
                    }
                });
        addEntry(
                new TitleValueRow() {

                    @Override
                    public String value(IndexableMarksWithEnergy state) {
                        if (state.getMarks() != null) {
                            return String.format("%f", state.getMarks().getPair().getEnergyTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String title() {
                        return "Energy Total - Pairs";
                    }
                });
        addEntry(
                new TitleValueRow() {

                    @Override
                    public String value(IndexableMarksWithEnergy state) {
                        if (state.getMarks() != null) {
                            return String.format("%f", state.getMarks().getAll().getEnergyTotal());
                        } else {
                            return "";
                        }
                    }

                    @Override
                    public String title() {
                        return "Energy Total - All";
                    }
                });
    }
}
