/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic;

import java.util.Iterator;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.plot.BoundedIndexContainerIterator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

class GraphInstanceBridge<T>
        implements FunctionWithException<MappedFrom<CSVStatistic>, GraphInstance, CreateException> {

    // START: PARAMETERS IN
    private GraphDefinition<T> graphDefinition;
    private BoundedIndexContainer<CSVStatistic> cntr;
    private FunctionWithException<CSVStatistic, T, ? extends Exception> elementBridge;
    // END: PARAMETERS IN

    private Optional<AxisLimits> rangeLimits = Optional.empty();

    public GraphInstanceBridge(
            GraphDefinition<T> graphDefinition,
            BoundedIndexContainer<CSVStatistic> cntr,
            FunctionWithException<CSVStatistic, T, ? extends Exception> elementBridge) {
        super();
        this.graphDefinition = graphDefinition;
        this.cntr = cntr;
        this.elementBridge = elementBridge;
    }

    @Override
    public GraphInstance apply(MappedFrom<CSVStatistic> sourceObject) throws CreateException {

        assert (graphDefinition != null);

        // To go from a particular csv statistic to a graph instance

        // lets get the current iter
        int currentIndex = sourceObject.getOriginalIter();

        // The bridge between CSVStats and the graph
        BoundedIndexContainerBridgeWithoutIndex<CSVStatistic, T, ? extends Exception> boundBridge =
                new BoundedIndexContainerBridgeWithoutIndex<>(cntr, elementBridge);

        AxisLimits domainLimits = createLimitsFromCntr(cntr);

        // We create a graph of all index points, so we can calculate range limits that are static
        //   only the first time we execute the function
        if (!rangeLimits.isPresent()) {
            rangeLimits = guessRangeLimits(boundBridge, domainLimits);
        }

        return graphDefinition.create(
                new BoundedIndexContainerIterator<>(boundBridge, 1000, currentIndex),
                Optional.of(domainLimits),
                rangeLimits);
    }

    private static AxisLimits createLimitsFromCntr(BoundedIndexContainer<CSVStatistic> cntr) {
        AxisLimits limits = new AxisLimits();
        limits.setAxisMin(cntr.getMinimumIndex());
        limits.setAxisMax(cntr.getMaximumIndex());
        return limits;
    }

    private Optional<AxisLimits> guessRangeLimits(
            BoundedIndexContainerBridgeWithoutIndex<CSVStatistic, T, ? extends Exception>
                    boundBridge,
            AxisLimits domainLimits)
            throws CreateException {
        Iterator<T> itrAll = new BoundedIndexContainerIterator<>(boundBridge, 1000);
        GraphInstance instance =
                graphDefinition.create(itrAll, Optional.of(domainLimits), Optional.empty());
        return instance.getRangeAxisLimits();
    }
}
