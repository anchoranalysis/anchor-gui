package org.anchoranalysis.gui.videostats.dropdown.manifest;

/*-
 * #%L
 * anchor-plugin-gui-import
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

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;

/** Performs the first operation. If it returns a null, performs the second-operation */
public class OperationReplaceNull<T> implements OperationWithProgressReporter<T> {

	private OperationWithProgressReporter<T> opFirst;
	private OperationWithProgressReporter<T> opSecond;
		
	public OperationReplaceNull(OperationWithProgressReporter<T> opFirst, OperationWithProgressReporter<T> opSecond) {
		super();
		this.opFirst = opFirst;
		this.opSecond = opSecond;
		assert(opFirst!=null);
		assert(opSecond!=null);
	}

	@Override
	public T doOperation(ProgressReporter progressReporter) throws ExecuteException {
		
		T first = opFirst.doOperation(progressReporter);
		
		if (first==null) {
			return opSecond.doOperation( ProgressReporterNull.get() );
		}
		
		return first;
	}

}
