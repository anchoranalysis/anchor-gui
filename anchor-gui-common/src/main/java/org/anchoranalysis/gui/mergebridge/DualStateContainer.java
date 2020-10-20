/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.mergebridge;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.bounded.BoundChangeListener;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.io.manifest.sequencetype.IncrementingIntegers;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;

// Contains both the selected and proposal histories
@RequiredArgsConstructor
public class DualStateContainer<T> implements BoundedIndexContainer<IndexableDualState<T>> {

    // START REQUIRED ARGUMENTS
    private final List<BoundedIndexContainer<IndexableMarksWithEnergy>> containers;
    private final TransformInstanteState<T> transformer;
    // END REQUIRED ARGUMENTS

    private IncrementingIntegers incrementalSequenceType;

    private LRUCache<Integer, IndexableDualState<T>> recentAccessCache;

    @FunctionalInterface
    public interface TransformInstanteState<T> {
        T transform(IndexableMarksWithEnergy state);
    }

    public void init() {

        this.recentAccessCache =
                new LRUCache<>(3, index -> new IndexableDualState<T>(index, instanceStates(index)));

        this.incrementalSequenceType = new IncrementingIntegers(maxOfMins(), 1);
        this.incrementalSequenceType.setEnd(minOfMaxs());
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        // We treat as static
    }

    @Override
    public int nextIndex(int index) {
        return this.incrementalSequenceType.elementRange().nextIndex(index);
    }

    @Override
    public int previousIndex(int index) {
        return this.incrementalSequenceType.elementRange().previousIndex(index);
    }

    @Override
    public int previousEqualIndex(int index) {
        return this.incrementalSequenceType.elementRange().previousEqualIndex(index);
    }

    @Override
    public int getMinimumIndex() {
        return this.incrementalSequenceType.elementRange().getMinimumIndex();
    }

    @Override
    public int getMaximumIndex() {
        return this.incrementalSequenceType.elementRange().getMaximumIndex();
    }

    @Override
    public IndexableDualState<T> get(int index) throws GetOperationFailedException {
        return recentAccessCache.get(index);
    }

    private int maxOfMins() {

        int maxOfMins = Integer.MIN_VALUE;

        for (BoundedIndexContainer<IndexableMarksWithEnergy> cntr : containers) {

            if (cntr.getMinimumIndex() > maxOfMins) {
                maxOfMins = cntr.getMinimumIndex();
            }
        }

        return maxOfMins;
    }

    private int minOfMaxs() {

        int minOfMaxs = Integer.MAX_VALUE;

        for (BoundedIndexContainer<IndexableMarksWithEnergy> container : containers) {

            if (container.getMaximumIndex() < minOfMaxs) {
                minOfMaxs = container.getMaximumIndex();
            }
        }

        return minOfMaxs;
    }

    private List<T> instanceStates(int index) throws GetOperationFailedException {
        return FunctionalList.mapToList(
                containers,
                GetOperationFailedException.class,
                container -> transformer.transform(nearestState(container, index)));
    }

    private static IndexableMarksWithEnergy nearestState(
            BoundedIndexContainer<IndexableMarksWithEnergy> container, int index)
            throws GetOperationFailedException {
        return container.get(container.previousEqualIndex(index));
    }
}
