/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;

public class GraphDefinitionLineIterVsKernelProp extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsKernelProp(
            KernelProposer<CfgNRGPixelized> kernelProposer,
            final int index,
            GraphColorScheme graphColorScheme) {

        super(
                "Rate of Kernel Proposal - "
                        + kernelProposer.getAllKernelFactories().get(index).getName(),
                new String[] {"Rate of Kernel Proposal"},
                "Acceptance Rate",
                new YValGetter<CSVStatistic>() {

                    @Override
                    public double getYVal(CSVStatistic item, int yIndex)
                            throws GetOperationFailedException {
                        return item.getKernelProp()[index];
                    }
                },
                graphColorScheme);
    }

    @Override
    public boolean isItemAccepted(CSVStatistic item) {
        return item.hasAccptProb();
    }
}
