/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic;

import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGraph;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class GraphDynamicallyDrawnFromCSVStatistic<T> extends CreateRasterGraph<T, CSVStatistic> {

    private FunctionWithException<CSVStatistic, T, CreateException> elementBridge;

    public GraphDynamicallyDrawnFromCSVStatistic(
            FunctionWithException<CSVStatistic, T, CreateException> elementBridge) {
        super();
        this.elementBridge = elementBridge;
    }

    @Override
    public IterableObjectGenerator<MappedFrom<CSVStatistic>, Stack> createGenerator(
            final ExportTaskParams params) throws CreateException {

        assert (getGraphDefinition() != null);

        try {
            FunctionWithException<MappedFrom<CSVStatistic>, GraphInstance, CreateException> bridge =
                    new GraphInstanceBridge<>(
                            getGraphDefinition(),
                            params.getFinderCsvStatistics().get(),
                            elementBridge);

            return new IterableObjectGeneratorBridge<>(createGraphInstanceGenerator(), bridge);

        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return params.getFinderCsvStatistics() != null;
    }
}
