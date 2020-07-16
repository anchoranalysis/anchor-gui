/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.arithmetic.DoubleUtilities;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarExecutionTimePerRejection
        extends GraphDefinitionBarKernelExecutionTime {

    public GraphDefinitionBarExecutionTimePerRejection(final String title) throws InitException {

        super(
                title,
                new String[] {"rejected", "not proposed", "accepted", "total"},
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (seriesNum) {
                        case 0:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getRejectedTime(), item.getRejectedCnt(), 0);
                        case 1:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getNotProposedTime(), item.getRejectedCnt(), 0);
                        case 2:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getAcceptedTime(), item.getRejectedCnt(), 0);
                        case 3:
                            return DoubleUtilities.divideByZeroReplace(
                                    item.getExecutionTime(), item.getRejectedCnt(), 0);
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "Execution Time (ms)",
                false);
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
