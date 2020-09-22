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

package org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorBridge;
import org.anchoranalysis.plot.PlotInstance;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.PlotGeneratorBase;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

@AllArgsConstructor
public class PlotFromCSVStatistic<T> extends PlotGeneratorBase<T, CSVStatistic> {

    private final CheckedFunction<CSVStatistic, T, CreateException> elementBridge;

    @Override
    public RasterGenerator<MappedFrom<CSVStatistic>> createGenerator(
            final ExportTaskParams params) throws CreateException {

        assert (getGraphDefinition() != null);

        try {
            CheckedFunction<MappedFrom<CSVStatistic>, PlotInstance, CreateException> bridge =
                    new GraphInstanceBridge<>(
                            getGraphDefinition(),
                            params.getFinderCsvStatistics().get(),
                            elementBridge);

            return new RasterGeneratorBridge<>(createPlotGenerator(), bridge);

        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return params.getFinderCsvStatistics() != null;
    }
}
