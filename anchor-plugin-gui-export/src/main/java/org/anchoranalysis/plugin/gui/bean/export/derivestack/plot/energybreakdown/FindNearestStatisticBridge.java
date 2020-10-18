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

package org.anchoranalysis.plugin.gui.bean.export.derivestack.plot.energybreakdown;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.plugin.gui.export.MappedFrom;

@AllArgsConstructor
class FindNearestStatisticBridge
        implements CheckedFunction<
                MappedFrom<IndexableMarksWithEnergy>,
                MappedFrom<CSVStatistic>,
                OperationFailedException> {

    private BoundedIndexContainer<CSVStatistic> container;

    @Override
    public MappedFrom<CSVStatistic> apply(MappedFrom<IndexableMarksWithEnergy> sourceObject)
            throws OperationFailedException {
        int indexAdj = container.previousEqualIndex(sourceObject.getOriginalIteration());

        try {
            CSVStatistic stats = container.get(indexAdj);

            return new MappedFrom<>(
                    sourceObject.getOriginalIteration(),
                    maybeDuplicate(stats, sourceObject.getOriginalIteration()));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    private CSVStatistic maybeDuplicate(CSVStatistic stats, int iterationToImpose) {
        // Duplicate and update iteration to match statistics
        if (stats.getIter() == iterationToImpose) {
            return stats;
        } else {
            return copyUpdateIter(stats, iterationToImpose);
        }
    }

    private CSVStatistic copyUpdateIter(CSVStatistic stats, int iterationToImpose) {
        CSVStatistic dup = stats.duplicate();
        dup.setIter(iterationToImpose);
        return dup;
    }
}
