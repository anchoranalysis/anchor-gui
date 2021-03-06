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
import org.anchoranalysis.mpp.segment.kernel.proposer.WeightedKernel;
import org.anchoranalysis.mpp.segment.kernel.proposer.WeightedKernelList;
import org.anchoranalysis.mpp.segment.optimization.feedback.aggregate.Aggregator;

@RequiredArgsConstructor
public class AcceptanceRate extends PlotDefinition {

    // START REQUIRED ARGUMENTS
    private final int windowSize;
    private final WeightedKernelList<?, ?> kernelFactoryList;
    // END REQUIRED ARGUMENTS

    private Aggregator aggregator;

    @Override
    public String title() {
        return "Acceptance Rate";
    }

    @Override
    public SimpleXYChartDescriptor descriptor() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(0, 500, 0, 0.001d, true, windowSize);

        descriptor.addLineItems("all");
        for (WeightedKernel<?, ?> kf : kernelFactoryList) {
            descriptor.addLineItems(kf.getName());
        }

        int numKernel = kernelFactoryList.size();

        int i = 0;
        String[] details = new String[3 + numKernel];
        details[i++] = "Iteration";
        details[i++] = "Time";
        details[i++] = "all";
        for (WeightedKernel<?, ?> kf : kernelFactoryList) {
            details[i++] = kf.getName();
        }

        descriptor.setDetailsItems(details);

        setTitleAndAxes(descriptor, title(), "time", "acceptance rate");

        return descriptor;
    }

    @Override
    public long[] valueArray(int iter, long timeStamp) {

        int numKernel = kernelFactoryList.size();

        int i = 0;
        long[] values = new long[1 + numKernel];
        values[i++] = resolve(this.aggregator.getAcceptAll());
        for (int j = 0; j < numKernel; j++) {
            values[i++] = resolve(aggregator.getKernelAccepted().get(j));
        }

        return values;
    }

    @Override
    public String[] detailsArray(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support) {

        int numKernel = kernelFactoryList.size();

        int i = 0;

        String[] details = new String[3 + numKernel];
        details[i++] = iter + "";
        details[i++] = support.formatTime(timeStamp - timeZoneOffset);
        details[i++] = String.format("%e", aggregator.getAcceptAll());
        for (int j = 0; j < numKernel; j++) {
            details[i++] = String.format("%.3f", aggregator.getKernelAccepted().get(j));
        }
        return details;
    }

    @Override
    public void updateCurrent(
            int iter, long timeStamp, MarksWithTotalEnergy current, Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void updateBest(int iter, long timeStamp, MarksWithTotalEnergy best) {
        // NOTHING TO DO
    }

    private static long resolve(double energy) {
        return (long) (1000 * energy);
    }
}
