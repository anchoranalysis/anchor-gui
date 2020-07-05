package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTimeAllEach;

/*-
 * #%L
 * anchor-gui-plot
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

import org.anchoranalysis.core.index.IIndexGetter;

public class IterAndExecutionTime implements IIndexGetter {

	private int iter;
	private KernelExecutionTimeAllEach executionTimes;
	
	// We expect an array where the first item is the total, and subsequent items
	//   are each separate kernel ID
	public IterAndExecutionTime(int iter, KernelExecutionTimeAllEach executionTimes ) {
		super();
		this.iter = iter;
		this.executionTimes = executionTimes;
	}

	public int getIter() {
		return iter;
	}

	public void setIter(int iter) {
		this.iter = iter;
	}

	@Override
	public int getIndex() {
		return iter;
	}

	public KernelExecutionTimeAllEach getExecutionTimes() {
		return executionTimes;
	}

	public void setExecutionTimes(KernelExecutionTimeAllEach executionTimes) {
		this.executionTimes = executionTimes;
	}
	
}
