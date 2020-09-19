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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator.energybreakdown.dynamically;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableSingleFileTypeGenerator;
import org.anchoranalysis.io.generator.IterableIntermediateGeneratorBridge;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.PlotGeneratorBase;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic.PlotFromCSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

@RequiredArgsConstructor
public abstract class PlotFromIndexableMarksBase<T>
        extends PlotGeneratorBase<T, IndexableMarksWithEnergy> {

    // START REQUIRED ARGUMENTS
    private final CheckedFunction<CSVStatistic, T, CreateException> elementBridge;
    // END REQUIRED ARGUMENTS

    // Delayed instantiation of delegate so parameters are already filled
    private PlotFromCSVStatistic<T> delegate;

    private PlotFromCSVStatistic<T> createDelegateIfNecessary() {
        if (delegate == null) {
            delegate = new PlotFromCSVStatistic<>(elementBridge);
            delegate.setHeight(getHeight());
            delegate.setWidth(getWidth());
            delegate.setGraphDefinition(getGraphDefinition());
        }
        return delegate;
    }

    @Override
    public IterableSingleFileTypeGenerator<MappedFrom<IndexableMarksWithEnergy>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException {

        IterableSingleFileTypeGenerator<MappedFrom<CSVStatistic>, Stack> generator =
                createDelegateIfNecessary().createGenerator(params);

        try {
            return new IterableIntermediateGeneratorBridge<>(
                    generator,
                    new FindNearestStatisticBridge(params.getFinderCsvStatistics().get()));

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return createDelegateIfNecessary().hasNecessaryParams(params)
                && params.getFinderCsvStatistics() != null;
    }
}
