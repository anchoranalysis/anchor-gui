/* (C)2020 */
package org.anchoranalysis.gui.plot.definition;

import com.sun.tools.visualvm.charts.SimpleXYChartDescriptor;
import com.sun.tools.visualvm.charts.SimpleXYChartSupport;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernel;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernelList;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public class AcceptanceRateGraphDefinition extends GraphDefinition {

    private WeightedKernelList<?> kernelFactoryList;

    private Aggregator agg;

    private int windowSize;

    public AcceptanceRateGraphDefinition(int windowSize, WeightedKernelList<?> kernelFactoryList) {
        super();
        this.kernelFactoryList = kernelFactoryList;
        this.windowSize = windowSize;
    }

    private long resolve(double nrg) {
        return (long) (1000 * nrg);
    }

    @Override
    public String title() {
        return "Acceptance Rate";
    }

    @Override
    public SimpleXYChartDescriptor descriptor() {
        SimpleXYChartDescriptor descriptor =
                SimpleXYChartDescriptor.decimal(0, 500, 0, 0.001d, true, windowSize);

        descriptor.addLineItems("all");
        for (WeightedKernel<?> kf : kernelFactoryList) {
            descriptor.addLineItems(kf.getName());
        }

        int numKernel = kernelFactoryList.size();

        int i = 0;
        String[] details = new String[3 + numKernel];
        details[i++] = "Iteration";
        details[i++] = "Time";
        details[i++] = "all";
        for (WeightedKernel<?> kf : kernelFactoryList) {
            details[i++] = kf.getName();
        }

        descriptor.setDetailsItems(details);

        setTitleAndAxes(descriptor, title(), "time", "acceptance rate");

        return descriptor;
    }

    @Override
    public long[] valueArr(int iter, long timeStamp) {

        int numKernel = kernelFactoryList.size();

        int i = 0;
        long[] values = new long[1 + numKernel];
        values[i++] = resolve(this.agg.getAccptAll());
        for (int j = 0; j < numKernel; j++) {
            values[i++] = resolve(agg.getKernelAccpt().get(j));
        }

        return values;
    }

    @Override
    public String[] detailsArr(
            int iter, long timeStamp, long timeZoneOffset, SimpleXYChartSupport support) {

        int numKernel = kernelFactoryList.size();

        int i = 0;

        String[] details = new String[3 + numKernel];
        details[i++] = iter + "";
        details[i++] = support.formatTime(timeStamp - timeZoneOffset);
        details[i++] = String.format("%e", agg.getAccptAll());
        for (int j = 0; j < numKernel; j++) {
            details[i++] = String.format("%.3f", agg.getKernelAccpt().get(j));
        }
        return details;
    }

    @Override
    public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg) {
        this.agg = agg;
    }

    @Override
    public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best) {}
}
