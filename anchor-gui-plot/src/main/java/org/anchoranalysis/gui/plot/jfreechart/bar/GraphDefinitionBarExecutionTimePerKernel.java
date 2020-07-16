/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarExecutionTimePerKernel
        extends GraphDefinitionBarKernelExecutionTime {

    public GraphDefinitionBarExecutionTimePerKernel(final String title) throws InitException {

        super(
                title,
                new String[] {"rejected", "not proposed", "accepted"},
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (seriesNum) {
                        case 0:
                            return item.getRejectedTime();
                        case 1:
                            return item.getNotProposedTime();
                        case 2:
                            return item.getAcceptedTime();
                        case 3:
                            // Just for total index, if re-used - we do not show
                            return item.getExecutionTime();
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "Execution Time (ms)",
                true);
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
