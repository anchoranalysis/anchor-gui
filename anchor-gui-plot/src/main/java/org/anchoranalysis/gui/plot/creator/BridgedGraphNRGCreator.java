/* (C)2020 */
package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsNRG;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsNRG.Item;

public class BridgedGraphNRGCreator
        extends BridgedGraphFromDualFinderCreator<GraphDefinitionLineIterVsNRG.Item> {

    public BridgedGraphNRGCreator() {}

    @Override
    public GraphDefinition<GraphDefinitionLineIterVsNRG.Item> createGraphDefinition(
            GraphColorScheme graphColorScheme) throws CreateException {

        GraphDefinitionLineIterVsNRG graphDefinition = new GraphDefinitionLineIterVsNRG();
        graphDefinition.setGraphColorScheme(graphColorScheme);
        return graphDefinition;
    }

    @Override
    public FunctionWithException<CSVStatistic, Item, CreateException> createCSVStatisticBridge() {
        return sourceObject ->
                new GraphDefinitionLineIterVsNRG.Item(
                        sourceObject.getIter(), sourceObject.getNrg());
    }

    @Override
    public FunctionWithException<CfgNRGInstantState, Item, CreateException>
            createCfgNRGInstantStateBridge() {
        return sourceObject -> {
            if (sourceObject.getCfgNRG() != null) {
                return new Item(sourceObject.getIndex(), sourceObject.getCfgNRG().getNrgTotal());
            } else {
                return new Item(sourceObject.getIndex(), Double.NaN);
            }
        };
    }
}
