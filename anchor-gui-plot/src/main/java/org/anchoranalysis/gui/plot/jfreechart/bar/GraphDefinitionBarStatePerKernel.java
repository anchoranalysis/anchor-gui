/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarStatePerKernel extends GraphDefinitionBarKernelExecutionTime {

    public GraphDefinitionBarStatePerKernel(final String title) throws InitException {

        super(
                title,
                new String[] {"rejected", "not proposed", "accepted"},
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (seriesNum) {
                        case 0:
                            return (double) item.getRejectedCnt();
                        case 1:
                            return (double) item.getNotProposedCnt();
                        case 2:
                            return (double) item.getAcceptedCnt();
                        case 3:
                            // Just for total index, if re-used - we do not show
                            return (double) item.getExecutionCnt();
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "# Iterations",
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
