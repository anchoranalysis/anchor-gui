/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class GraphDefinitionLineIterVsAccptProbAll extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsAccptProbAll(GraphColorScheme graphColorScheme) {

        super(
                "Rate of Kernel Acceptance (All acceptances)",
                new String[] {"Accepted Probability"},
                "Acceptance Rate",
                new YValGetter<CSVStatistic>() {

                    @Override
                    public double getYVal(CSVStatistic item, int yIndex)
                            throws GetOperationFailedException {
                        return item.getAccptProbAll();
                    }
                },
                graphColorScheme);
    }

    @Override
    public boolean isItemAccepted(CSVStatistic item) {
        return item.hasAccptProbAll();
    }
}
