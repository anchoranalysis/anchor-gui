package org.anchoranalysis.gui.backgroundset;

/*-
 * #%L
 * anchor-gui-common
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

class TimeSequenceBridge implements IBoundedIndexContainer<Stack> {

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
		if (index < (src.size()-1)) {
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
