/* (C)2020 */
package org.anchoranalysis.gui.backgroundset;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

class TimeSequenceBridge implements BoundedIndexContainer<Stack> {

    private TimeSequence src;

    public TimeSequenceBridge(TimeSequence src) {
        super();
        this.src = src;
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {
        // NOTHING TO DO, as bounds will never change
    }

    @Override
    public int nextIndex(int index) {
        if (index < (src.size() - 1)) {
            return index + 1;
        } else {
            return -1;
        }
    }

    @Override
    public int previousIndex(int index) {
        return index - 1;
    }

    @Override
    public int previousEqualIndex(int index) {
        return index;
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return src.size() - 1;
    }

    @Override
    public Stack get(int index) throws GetOperationFailedException {
        return src.get(index);
    }
}
