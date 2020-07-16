/* (C)2020 */
package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsCfgSize;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsCfgSize.Item;

public class BridgedGraphCfgSizeCreator
        extends BridgedGraphFromDualFinderCreator<GraphDefinitionLineIterVsCfgSize.Item> {

    @Override
    public GraphDefinition<GraphDefinitionLineIterVsCfgSize.Item> createGraphDefinition(
            GraphColorScheme graphColorScheme) throws CreateException {
        GraphDefinitionLineIterVsCfgSize graphDefinition = new GraphDefinitionLineIterVsCfgSize();
        graphDefinition.setGraphColorScheme(graphColorScheme);
        return graphDefinition;
    }

    @Override
    public FunctionWithException<CSVStatistic, Item, CreateException> createCSVStatisticBridge() {
        return statistic ->
                new GraphDefinitionLineIterVsCfgSize.Item(statistic.getIter(), statistic.getSize());
    }

    @Override
    public FunctionWithException<CfgNRGInstantState, Item, CreateException>
            createCfgNRGInstantStateBridge() {
        return sourceObject -> {
            if (sourceObject.getCfgNRG() != null) {
                return new GraphDefinitionLineIterVsCfgSize.Item(
                        sourceObject.getIndex(), sourceObject.getCfgNRG().getCfg().size());
            } else {
                return new GraphDefinitionLineIterVsCfgSize.Item(
                        sourceObject.getIndex(), Double.NaN);
            }
        };
    }
}
