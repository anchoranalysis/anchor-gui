/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

class FindNearestStatisticBridge
        implements FunctionWithException<
                MappedFrom<CfgNRGInstantState>,
                MappedFrom<CSVStatistic>,
                OperationFailedException> {

    private BoundedIndexContainer<CSVStatistic> cntr;

    public FindNearestStatisticBridge(BoundedIndexContainer<CSVStatistic> cntr) {
        super();
        this.cntr = cntr;
    }

    @Override
    public MappedFrom<CSVStatistic> apply(MappedFrom<CfgNRGInstantState> sourceObject)
            throws OperationFailedException {
        int indexAdj = cntr.previousEqualIndex(sourceObject.getOriginalIter());

        try {
            CSVStatistic stats = cntr.get(indexAdj);

            return new MappedFrom<>(
                    sourceObject.getOriginalIter(),
                    maybeDuplicate(stats, sourceObject.getOriginalIter()));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    private CSVStatistic maybeDuplicate(CSVStatistic stats, int iterToImpose) {
        // Duplicate and update iteration to match statistics
        if (stats.getIter() == iterToImpose) {
            return stats;
        } else {
            return copyUpdateIter(stats, iterToImpose);
        }
    }

    private CSVStatistic copyUpdateIter(CSVStatistic stats, int iterToImpose) {
        CSVStatistic dup = stats.duplicate();
        dup.setIter(iterToImpose);
        return dup;
    }
}
