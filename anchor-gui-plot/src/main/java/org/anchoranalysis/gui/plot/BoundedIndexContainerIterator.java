/* (C)2020 */
package org.anchoranalysis.gui.plot;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedExceptionRuntime;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;

public class BoundedIndexContainerIterator<T> implements Iterator<T> {

    private final BoundedIndexContainer<T> container;
    private final int increment;
    private final int maxIndex;

    private int currentIndex;

    /**
     * Create an interator over a bounded index container
     *
     * @param container the container providing a range over which iteration is possible
     * @param numPoints maximum number of sub-divisions
     */
    public BoundedIndexContainerIterator(BoundedIndexContainer<T> container, int numPoints) {
        this(container, numPoints, container.getMaximumIndex());
    }

    public BoundedIndexContainerIterator(
            BoundedIndexContainer<T> container, int numPoints, int maxIndex) {
        super();
        this.container = container;
        this.maxIndex = maxIndex;

        int range = container.getMaximumIndex() - container.getMinimumIndex();
        increment = Math.max(range / numPoints, 1);

        currentIndex = container.getMinimumIndex();
    }

    @Override
    public boolean hasNext() {
        return (currentIndex <= this.maxIndex);
    }

    @Override
    public T next() {
        try {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T item = container.get(container.previousEqualIndex(currentIndex));
            currentIndex += increment;
            return item;
        } catch (GetOperationFailedException e) {
            throw new GetOperationFailedExceptionRuntime(e);
        }
    }

    @Override
    public void remove() {
        // NOT IMPLEMENTED
    }
}
