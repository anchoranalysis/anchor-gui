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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.manifest.csvstatistic.CSVStatistic;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

class FindNearestStatisticBridge
        implements CheckedFunction<
                MappedFrom<IndexableMarksWithEnergy>,
                MappedFrom<CSVStatistic>,
                OperationFailedException> {

    private BoundedIndexContainer<CSVStatistic> cntr;

    public FindNearestStatisticBridge(BoundedIndexContainer<CSVStatistic> cntr) {
        super();
        this.cntr = cntr;
    }

    @Override
    public MappedFrom<CSVStatistic> apply(MappedFrom<IndexableMarksWithEnergy> sourceObject)
            throws OperationFailedException {
        int indexAdj = cntr.previousEqualIndex(sourceObject.getOriginalIter());

        try {
            CSVStatistic stats = cntr.get(indexAdj);

            return new MappedFrom<>(
                    sourceObject.getOriginalIter(),
                    maybeDuplicate(stats, sourceObject.getOriginalIter()));
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    private CSVStatistic maybeDuplicate(CSVStatistic stats, int iterToImpose) {
        // Duplicate and update iteration to match statistics
        if (stats.getIter() == iterToImpose) {
            return stats;
        } else {
            return copyUpdateIter(stats, iterToImpose);
        }
    }

    private CSVStatistic copyUpdateIter(CSVStatistic stats, int iterToImpose) {
        CSVStatistic dup = stats.duplicate();
        dup.setIter(iterToImpose);
        return dup;
    }
}
