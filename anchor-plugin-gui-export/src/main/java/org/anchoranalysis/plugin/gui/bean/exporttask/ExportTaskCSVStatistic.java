/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class ExportTaskCSVStatistic
        extends ExportTaskRasterGeneratorFromBoundedIndexContainer<CSVStatistic> {

    private static class ExportTaskParamsCSVStatisticContainerBridge
            implements FunctionWithException<
                    ExportTaskParams,
                    BoundedIndexContainer<CSVStatistic>,
                    OperationFailedException> {

        @Override
        public BoundedIndexContainer<CSVStatistic> apply(ExportTaskParams sourceObject)
                throws OperationFailedException {
            try {
                return sourceObject.getFinderCsvStatistics().get();
            } catch (GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    public ExportTaskCSVStatistic() {
        super();
    }

    @Override
    public void init() {
        setBridge(new ExportTaskParamsCSVStatisticContainerBridge());
    }
}
