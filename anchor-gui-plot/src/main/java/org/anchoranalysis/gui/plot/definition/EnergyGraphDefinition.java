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
import org.anchoranalysis.mpp.feature.energy.marks.MarksWithTotalEnergy;
import org.anchoranalysis.mpp.segment.optscheme.feedback.aggregate.Aggregator;

@RequiredArgsConstructor
public class EnergyGraphDefinition extends GraphDefinition {

    // START REQUIRED ARGUMENTS
    private final int windowSize;
    // END REQUIRED ARGUMENTS

    private double energyCurrent;
    private double energyBest;

    @Override
    public String title() {
        return "-log Energy";
    }

    @Override
    public SimpleXYChartDescriptor descriptor() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(-10, 10, -10, 0.01d, true, windowSize);
        descriptor.addLineFillItems("-log Energy (best)");
        descriptor.addLineFillItems("-log Energy (current)");

        descriptor.setDetailsItems(
                new String[] {"Iteration", "Time", "-log Energy (best)", "-log Energy (current)"});

        setTitleAndAxes(descriptor, title(), "time", "energy");

        return descriptor;
    }

    @Override
    public long[] valueArray(int iter, long timeStamp) {

        long[] values = new long[2];
        values[0] = resolve(this.energyBest);
        values[1] = resolve(this.energyCurrent);
        return values;
    }

    @Override
    public String[] detailsArray(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support) {
        return new String[] {
            iter + "",
            support.formatTime(timeStamp - timeZoneOffset),
            String.format("%e", this.energyBest),
            String.format("%e", this.energyCurrent)
        };
    }

    @Override
    public void updateCurrent(
            int iter, long timeStamp, MarksWithTotalEnergy current, Aggregator aggregator) {
        this.energyCurrent = aggregator.getEnergy();
    }

    @Override
    public void updateBest(int iter, long timeStamp, MarksWithTotalEnergy best) {
        this.energyBest = best.getEnergyTotal();
    }

    private static long resolve(double energy) {
        return (long) (100 * energy);
    }
}
