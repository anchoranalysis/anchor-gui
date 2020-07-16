/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;

public class GraphDefinitionLineIterVsKernelAccptProb
        extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsKernelAccptProb(
            KernelProposer<CfgNRGPixelized> kernelProposer,
            int index,
            GraphColorScheme graphColorScheme) {
        this(
                kernelProposer.getAllKernelFactories().get(index).getName(),
                new String[] {kernelProposer.getAllKernelFactories().get(index).getName()},
                new SpecificKernelAccptCSVStatistic(index),
                graphColorScheme);
    }

    public GraphDefinitionLineIterVsKernelAccptProb(
            String subtitle,
            String[] seriesNames,
            final YValGetter<CSVStatistic> yValGetter,
            GraphColorScheme graphColorScheme) {

        super(
                "Rate of Kernel Acceptance - " + subtitle,
                subtitle,
                seriesNames,
                "Acceptance Rate",
                yValGetter,
                graphColorScheme);
    }

    @Override
    public boolean isItemAccepted(CSVStatistic item) {
        return item.hasAccptProb();
    }
}
