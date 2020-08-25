/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.mpp.feature.energy.marks.VoxelizedMarksWithEnergy;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.mpp.segment.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.plot.index.LinePlot.YValGetter;

public class GraphDefinitionLineIterVsKernelAccptProb
        extends GraphDefinitionLineIterVsCSVStatistic {

    public GraphDefinitionLineIterVsKernelAccptProb(
            KernelProposer<VoxelizedMarksWithEnergy> kernelProposer,
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
