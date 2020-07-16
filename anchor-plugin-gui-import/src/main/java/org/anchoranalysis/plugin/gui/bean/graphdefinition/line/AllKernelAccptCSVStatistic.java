/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class AllKernelAccptCSVStatistic implements YValGetter<CSVStatistic> {

    private int shift;

    public AllKernelAccptCSVStatistic() {
        this(0);
    }

    public AllKernelAccptCSVStatistic(int shift) {
        super();
        this.shift = shift;
    }

    @Override
    public double getYVal(CSVStatistic item, int yIndex) throws GetOperationFailedException {
        return item.getKernelAccpt()[yIndex + shift];
    }
}
