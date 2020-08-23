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

package org.anchoranalysis.gui.plot.definition;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.MarksWithTotalEnergy;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

@RequiredArgsConstructor
public class CfgSizeGraphDefinition extends GraphDefinition {

    // START REQUIRED ARGUMENTS
    private final int windowSize;
    // END REQUIRED ARGUMENTS
    
    private double sizeCurrent;
    private int sizeBest;

    @Override
    public String title() {
        return "Configuration Size";
    }

    @Override
    public SimpleXYChartDescriptor descriptor() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(0, 100, 0, 1d, true, windowSize);
        descriptor.addLineItems("Configuration Size (best)");
        descriptor.addLineItems("Configuration Size (current)");

        descriptor.setDetailsItems(
                new String[] {
                    "Iteration", "Time", "Configuration Size (best)", "Configuration Size (current)"
                });

        setTitleAndAxes(descriptor, title(), "time", "number");

        return descriptor;
    }

    @Override
    public long[] valueArray(int iter, long timeStamp) {

        long[] values = new long[2];
        values[0] = this.sizeBest;
        values[1] = (long) this.sizeCurrent;
        return values;
    }

    @Override
    public String[] detailsArray(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support) {
        return new String[] {
            iter + "",
            support.formatTime(timeStamp - timeZoneOffset),
            String.format("%4.1f", (double) this.sizeBest),
            String.format("%4.1f", this.sizeCurrent)
        };
    }

    @Override
    public void updateCurrent(int iter, long timeStamp, MarksWithTotalEnergy crnt, Aggregator agg) {
        this.sizeCurrent = agg.getSize();
    }

    @Override
    public void updateBest(int iter, long timeStamp, MarksWithTotalEnergy best) {
        this.sizeBest = best.size();
    }
}
