/*-
 * #%L
 * anchor-gui-plot
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

package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.anchor.plot.bean.Plot;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.plot.definition.line.LinePlotIterationVsEnergy;
import org.anchoranalysis.gui.plot.definition.line.LinePlotIterationVsEnergy.Item;

public class BridgedPlotEnergyCreator
        extends BridgedPlotFromDualFinderCreator<LinePlotIterationVsEnergy.Item> {

    @Override
    public Plot<LinePlotIterationVsEnergy.Item> createGraphDefinition(
            GraphColorScheme graphColorScheme) throws CreateException {

        LinePlotIterationVsEnergy graphDefinition = new LinePlotIterationVsEnergy();
        graphDefinition.setGraphColorScheme(graphColorScheme);
        return graphDefinition;
    }

    @Override
    public CheckedFunction<CSVStatistic, Item, CreateException> createCSVStatisticBridge() {
        return sourceObject ->
                new LinePlotIterationVsEnergy.Item(
                        sourceObject.getIter(), sourceObject.getEnergy());
    }

    @Override
    public CheckedFunction<IndexableMarksWithEnergy, Item, CreateException>
            createMarksBridge() {
        return sourceObject -> {
            if (sourceObject.getMarks() != null) {
                return new Item(sourceObject.getIndex(), sourceObject.getMarks().getEnergyTotal());
            } else {
                return new Item(sourceObject.getIndex(), Double.NaN);
            }
        };
    }
}
