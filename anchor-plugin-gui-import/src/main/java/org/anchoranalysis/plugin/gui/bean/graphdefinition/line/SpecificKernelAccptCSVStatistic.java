/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class SpecificKernelAccptCSVStatistic implements YValGetter<CSVStatistic> {

    private int index;

    public SpecificKernelAccptCSVStatistic(int index) {
        super();
        this.index = index;
    }

    @Override
    public double getYVal(CSVStatistic item, int yIndex) throws GetOperationFailedException {
        return item.getKernelAccpt()[index];
    }
}
