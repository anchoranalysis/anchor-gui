/* (C)2020 */
package org.anchoranalysis.gui.mergebridge;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;

// Contains both the selected and proposal histories
@RequiredArgsConstructor
public class DualCfgNRGContainer<T> implements BoundedIndexContainer<IndexedDualState<T>> {

    // START REQUIRED ARGUMENTS
    private final List<BoundedIndexContainer<CfgNRGInstantState>> cntrs;
    private final TransformInstanteState<T> transformer;
    // END REQUIRED ARGUMENTS

    private IncrementalSequenceType incrementalSequenceType;

    private LRUCache<Integer, IndexedDualState<T>> recentAccessCache;

    @FunctionalInterface
    public interface TransformInstanteState<T> {
        T transform(CfgNRGInstantState state);
    }

    public void init() {

        this.recentAccessCache =
                new LRUCache<>(3, index -> new IndexedDualState<T>(index, instanceStates(index)));

        this.incrementalSequenceType = new IncrementalSequenceType();
        this.incrementalSequenceType.setStart(maxOfMins());
        this.incrementalSequenceType.setIncrementSize(1);
        this.incrementalSequenceType.setEnd(minOfMaxs());
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        // We treat as static
    }

    @Override
    public int nextIndex(int index) {
        return this.incrementalSequenceType.nextIndex(index);
    }

    @Override
    public int previousIndex(int index) {
        return this.incrementalSequenceType.previousIndex(index);
    }

    @Override
    public int previousEqualIndex(int index) {
        return this.incrementalSequenceType.previousEqualIndex(index);
    }

    @Override
    public int getMinimumIndex() {
        return this.incrementalSequenceType.getMinimumIndex();
    }

    @Override
    public int getMaximumIndex() {
        return this.incrementalSequenceType.getMaximumIndex();
    }

    @Override
    public IndexedDualState<T> get(int index) throws GetOperationFailedException {
        return recentAccessCache.get(index);
    }

    private int maxOfMins() {

        int maxOfMins = Integer.MIN_VALUE;

        for (BoundedIndexContainer<CfgNRGInstantState> cntr : cntrs) {

            if (cntr.getMinimumIndex() > maxOfMins) {
                maxOfMins = cntr.getMinimumIndex();
            }
        }

        return maxOfMins;
    }

    private int minOfMaxs() {

        int minOfMaxs = Integer.MAX_VALUE;

        for (BoundedIndexContainer<CfgNRGInstantState> cntr : cntrs) {

            if (cntr.getMaximumIndex() < minOfMaxs) {
                minOfMaxs = cntr.getMaximumIndex();
            }
        }

        return minOfMaxs;
    }

    private List<T> instanceStates(int index) throws GetOperationFailedException {
        return FunctionalList.mapToList(
                cntrs,
                GetOperationFailedException.class,
                cntr -> transformer.transform(nearestState(cntr, index)));
    }

    private static CfgNRGInstantState nearestState(
            BoundedIndexContainer<CfgNRGInstantState> cntr, int index)
            throws GetOperationFailedException {
        return cntr.get(cntr.previousEqualIndex(index));
    }
}
