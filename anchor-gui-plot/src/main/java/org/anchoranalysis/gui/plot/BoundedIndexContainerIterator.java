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
		this( container, numPoints, container.getMaximumIndex() );
	}
	
	public BoundedIndexContainerIterator(BoundedIndexContainer<T> container, int numPoints, int maxIndex) {
		super();
		this.container = container;
		this.maxIndex = maxIndex;
			
		int range = container.getMaximumIndex() - container.getMinimumIndex();
    	increment = Math.max(range/numPoints, 1);
    	
    	currentIndex = container.getMinimumIndex();
	}

	@Override
	public boolean hasNext() {
		return (currentIndex<=this.maxIndex);
	}

	@Override
	public T next() {
		try {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			
			T item = container.get(
				container.previousEqualIndex(currentIndex)
			);
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
