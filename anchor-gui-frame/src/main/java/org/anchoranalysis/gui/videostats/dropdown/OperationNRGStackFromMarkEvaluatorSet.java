package org.anchoranalysis.gui.videostats.dropdown;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;

public class OperationNRGStackFromMarkEvaluatorSet implements OperationWithProgressReporter<NRGStackWithParams>, IUpdatableMarkEvaluator {

	private MarkEvaluatorSetForImage markEvaluatorSet;
	private String markEvaluatorIdentifier;
	
	public OperationNRGStackFromMarkEvaluatorSet( MarkEvaluatorSetForImage markEvaluatorSet ) {
		super();
		this.markEvaluatorSet = markEvaluatorSet;
	}

	@Override
	public NRGStackWithParams doOperation( ProgressReporter progressReporter ) throws ExecuteException {
		
		if (markEvaluatorIdentifier==null || markEvaluatorIdentifier.isEmpty()) {
			return null;
		}
		
		try {
			return markEvaluatorSet.get( markEvaluatorIdentifier ).getNRGStack();
		} catch (IllegalArgumentException | GetOperationFailedException e) {
			throw new ExecuteException(e);
		}
	}

	@Override
	public void setMarkEvaluatorIdentifier(String markEvaluatorIdentifier) {
		this.markEvaluatorIdentifier = markEvaluatorIdentifier;
	}
}