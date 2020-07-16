/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class GraphDefinitionLineIterVsTimePerIter extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsTimePerIter(GraphColorScheme graphColorScheme) {

        super(
                "Execution Time per Iter",
                new String[] {"Time"},
                "Seconds",
                new YValGetter<CSVStatistic>() {

                    @Override
                    public double getYVal(CSVStatistic item, int yIndex)
                            throws GetOperationFailedException {
                        return item.getTimePerIter();
                    }
                },
                graphColorScheme);
    }

    @Override
    public boolean isItemAccepted(CSVStatistic item) {
        return item.hasTemperature();
    }
}
