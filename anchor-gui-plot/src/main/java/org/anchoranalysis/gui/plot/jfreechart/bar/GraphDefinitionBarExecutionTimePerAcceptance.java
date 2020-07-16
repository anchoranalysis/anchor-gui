/*-
 * #%L
 * anchor-gui-plot
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

package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.arithmetic.DoubleUtilities;

public class GraphDefinitionBarExecutionTimePerAcceptance
        extends GraphDefinitionBarKernelExecutionTime {

    public GraphDefinitionBarExecutionTimePerAcceptance(final String title) {

        super(
                title,
                new String[] {"rejected", "not proposed", "accepted", "total"},
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (seriesNum) {
                        case 0:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getRejectedTime(), item.getAcceptedCnt(), 0);
                        case 1:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getNotProposedTime(), item.getAcceptedCnt(), 0);
                        case 2:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getAcceptedTime(), item.getAcceptedCnt(), 0);
                        case 3:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getExecutionTime(), item.getAcceptedCnt(), 0);
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "Execution Time (ms)",
                false);
    }

    @Override
    public String getShortTitle() {
        return getTitle();
    }

    @Override
    public int totalIndex() {
        return 3;
    }
}
