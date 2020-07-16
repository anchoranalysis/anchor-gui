/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.arithmetic.DoubleUtilities;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarAcceptanceRatePerKernel
        extends GraphDefinitionBarKernelExecutionTime {

    public GraphDefinitionBarAcceptanceRatePerKernel(final String title) throws InitException {

        super(
                title,
                new String[] {
                    "acceptance rate",
                },
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (seriesNum) {
                        case 0:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getAcceptedCnt(), item.getExecutionCnt(), 0);
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "Acceptance Rate",
                true);
    }

    @Override
    public String getShortTitle() {
        return "Rate of Acceptance";
    }

    @Override
    public int totalIndex() {
        return 0;
    }
}
