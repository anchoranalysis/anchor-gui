/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class GraphDefinitionLineIterVsAccptProbMultipleSeries
        extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsAccptProbMultipleSeries(GraphColorScheme graphColorScheme) {

        super(
                "Rate of Kernel Acceptance (Multiple series)",
                new String[] {"First", "All", "Random"},
                "Acceptance Rate",
                (CSVStatistic item, int yIndex) -> {
                    switch (yIndex) {
                        case 0:
                            return item.getAccptProb();
                        case 1:
                            return item.getAccptProbAll();
                        case 2:
                            return item.getAccptProbRand();
                        default:
                            throw new GetOperationFailedException("Invalid yIndex value");
                    }
                },
                graphColorScheme);
    }

    @Override
    public boolean isItemAccepted(CSVStatistic item) {
        return item.hasAccptProb() && item.hasAccptProbAll() && item.hasAccptProbRand();
    }
}
