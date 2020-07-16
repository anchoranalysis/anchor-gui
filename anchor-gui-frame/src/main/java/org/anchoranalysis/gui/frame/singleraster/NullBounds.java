/* (C)2020 */
package org.anchoranalysis.gui.frame.singleraster;

import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;

class NullBounds implements BoundedRangeIncompleteDynamic {

    @Override
    public int nextIndex(int index) {
        return 0;
    }

    @Override
    public int previousIndex(int index) {
        return 0;
    }

    @Override
    public int previousEqualIndex(int index) {
        return 0;
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return 0;
    }

    @Override
    public void addBoundChangeListener(BoundChangeListener cl) {}
}
