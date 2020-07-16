/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class GraphDefinitionLineIterVsTime extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsTime(GraphColorScheme graphColorScheme) {

        super(
                "Total Execution Time",
                new String[] {"Time"},
                "Seconds",
                new YValGetter<CSVStatistic>() {

                    @Override
                    public double getYVal(CSVStatistic item, int yIndex)
                            throws GetOperationFailedException {
                        return item.getTime();
                    }
                },
                graphColorScheme);
    }

    @Override
    public boolean isItemAccepted(CSVStatistic item) {
        return item.hasTemperature();
    }
}
