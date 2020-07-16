/* (C)2020 */
package org.anchoranalysis.gui.plot.definition;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public class ExecutionTimeGraphDefinition extends GraphDefinition {

    private double msPerIter = -1;
    private long lastTimeStamp = -1;
    private int divider;

    private int windowSize;

    public ExecutionTimeGraphDefinition(int windowSize) {
        super();
        this.windowSize = windowSize;
    }

    private long resolve(double nrg) {
        return (long) (100 * nrg);
    }

    @Override
    public String title() {
        return "Execution Time";
    }

    @Override
    public SimpleXYChartDescriptor descriptor() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(0, 1000, 0, 0.01d, true, windowSize);
        descriptor.addLineFillItems("ms per iteration");

        descriptor.setDetailsItems(
                new String[] {"Iteration", "Time", "ms per iteration", "Last divider"});

        setTitleAndAxes(descriptor, title(), "time", "ms per iteration");

        return descriptor;
    }

    @Override
    public long[] valueArr(int iter, long timeStamp) {

        long[] values = new long[1];
        values[0] = resolve(this.msPerIter);
        return values;
    }

    @Override
    public String[] detailsArr(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support) {
        return new String[] {
            iter + "",
            support.formatTime(timeStamp - timeZoneOffset),
            String.format("%e", this.msPerIter),
            String.format("%d", this.divider)
        };
    }

    @Override
    public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg) {

        if (lastTimeStamp != -1 && agg.hasLastDivider()) {

            int divider = agg.getLastDivider();

            long timeDiff = timeStamp - lastTimeStamp;
            this.msPerIter = ((double) timeDiff) / divider;
            this.divider = divider;
        }

        lastTimeStamp = timeStamp;
    }

    @Override
    public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best) {}
}
