/* (C)2020 */
package org.anchoranalysis.gui.plot.definition;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public class TemperatureGraphDefinition extends GraphDefinition {

    private double temperature;

    private int windowSize;

    public TemperatureGraphDefinition(int windowSize) {
        super();
        this.windowSize = windowSize;
    }

    private long resolve(double nrg) {
        return (long) (100 * nrg);
    }

    @Override
    public String title() {
        return "Temperature";
    }

    @Override
    public SimpleXYChartDescriptor descriptor() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(0, 100, 0, 0.01d, true, windowSize);
        descriptor.addLineFillItems("Temperature");

        descriptor.setDetailsItems(new String[] {"Iteration", "Time", "Temperature"});

        setTitleAndAxes(descriptor, title(), "time", "temperature");

        return descriptor;
    }

    @Override
    public long[] valueArr(int iter, long timeStamp) {

        long[] values = new long[1];
        values[0] = resolve(this.temperature);
        return values;
    }

    @Override
    public String[] detailsArr(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support) {
        return new String[] {
            iter + "",
            support.formatTime(timeStamp - timeZoneOffset),
            String.format("%e", this.temperature),
        };
    }

    @Override
    public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg) {
        this.temperature = agg.getTemp();
    }

    @Override
    public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best) {}
}
