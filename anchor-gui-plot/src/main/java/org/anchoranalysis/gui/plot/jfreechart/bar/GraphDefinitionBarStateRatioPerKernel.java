/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.arithmetic.DoubleUtilities;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarStateRatioPerKernel extends GraphDefinitionBarKernelExecutionTime {

    public GraphDefinitionBarStateRatioPerKernel(final String title) throws InitException {

        super(
                title,
                new String[] {"rejection", "non-proposal", "acceptance"},
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (seriesNum) {
                        case 0:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getRejectedCnt(), item.getExecutionCnt(), 0);
                        case 1:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getNotProposedCnt(), item.getExecutionCnt(), 0);
                        case 2:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getAcceptedCnt(), item.getExecutionCnt(), 0);
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "Ratio of Kernel Proposals",
                true);
    }

    @Override
    public String getShortTitle() {
        return getTitle();
    }

    @Override
    public int totalIndex() {
        return -1;
    }
}
