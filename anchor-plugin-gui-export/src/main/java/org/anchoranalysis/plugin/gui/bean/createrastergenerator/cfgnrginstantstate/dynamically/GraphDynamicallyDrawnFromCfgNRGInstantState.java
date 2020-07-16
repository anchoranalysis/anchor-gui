/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGraph;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic.GraphDynamicallyDrawnFromCSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public abstract class GraphDynamicallyDrawnFromCfgNRGInstantState<T>
        extends CreateRasterGraph<T, CfgNRGInstantState> {

    // Delayed instantiation of delegate so parameters are already filled
    private GraphDynamicallyDrawnFromCSVStatistic<T> delegate;

    private FunctionWithException<CSVStatistic, T, CreateException> elementBridge;

    public GraphDynamicallyDrawnFromCfgNRGInstantState(
            FunctionWithException<CSVStatistic, T, CreateException> elementBridge) {
        super();
        this.elementBridge = elementBridge;
    }

    private GraphDynamicallyDrawnFromCSVStatistic<T> createDelegateIfNecessary() {
        if (delegate == null) {
            delegate = new GraphDynamicallyDrawnFromCSVStatistic<>(elementBridge);
            delegate.setHeight(getHeight());
            delegate.setWidth(getWidth());
            delegate.setGraphDefinition(getGraphDefinition());
        }
        return delegate;
    }

    @Override
    public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException {

        IterableObjectGenerator<MappedFrom<CSVStatistic>, Stack> generator =
                createDelegateIfNecessary().createGenerator(params);

        try {
            return new IterableObjectGeneratorBridge<>(
                    generator,
                    new FindNearestStatisticBridge(params.getFinderCsvStatistics().get()));

        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return createDelegateIfNecessary().hasNecessaryParams(params)
                && params.getFinderCsvStatistics() != null;
    }
}
