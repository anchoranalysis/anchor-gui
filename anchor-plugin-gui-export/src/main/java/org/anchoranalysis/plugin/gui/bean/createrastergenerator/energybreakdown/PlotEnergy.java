/*-
 * #%L
 * anchor-plugin-gui-export
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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator.energybreakdown;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.plot.EnergyGraphItem;
import org.anchoranalysis.gui.plot.creator.GeneratePlotEnergy;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableSingleFileTypeGenerator;
import org.anchoranalysis.io.generator.IterableSingleFileTypeGeneratorBridge;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.plot.PlotInstance;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.PlotGeneratorBase;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class PlotEnergy extends PlotGeneratorBase<EnergyGraphItem, IndexableMarksWithEnergy> {

    @Override
    public IterableSingleFileTypeGenerator<MappedFrom<IndexableMarksWithEnergy>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException {

        IterableSingleFileTypeGenerator<PlotInstance, Stack> generator = createGraphInstanceGenerator();

        return new IterableSingleFileTypeGeneratorBridge<>(
                createBridge(generator, params), MappedFrom::getObject);
    }

    private IterableSingleFileTypeGeneratorBridge<Stack, IndexableMarksWithEnergy, ClickableGraphInstance>
            createBridge(
                    IterableSingleFileTypeGenerator<PlotInstance, Stack> generator,
                    ExportTaskParams params) {
        // Presents a generator for a GraphInstance as a generator for ClickableGraphInstance
        IterableSingleFileTypeGeneratorBridge<Stack, ClickableGraphInstance, PlotInstance>
                clickableGenerator =
                        new IterableSingleFileTypeGeneratorBridge<>(
                                generator, ClickableGraphInstance::getGraphInstance);

        // Presents a generator for a ClickableGraphInstance as a generator for Stack
        return new IterableSingleFileTypeGeneratorBridge<>(
                clickableGenerator,
                new GeneratePlotEnergy(getGraphDefinition(), params.getColorIndexMarks()));
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return params.getColorIndexMarks() != null;
    }
}
