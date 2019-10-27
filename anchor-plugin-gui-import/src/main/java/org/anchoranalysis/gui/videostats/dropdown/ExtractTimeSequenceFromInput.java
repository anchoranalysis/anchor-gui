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
import org.anchoranalysis.core.cache.IdentityOperation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogUtilities;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.StackSequenceInput;
import org.anchoranalysis.image.stack.TimeSequence;

public class ExtractTimeSequenceFromInput extends CachedOperationWithProgressReporter<TimeSequenceProvider> {

	private StackSequenceInput inputObject;
	private int seriesNum;
	
	public ExtractTimeSequenceFromInput( StackSequenceInput inputObject ) {
		this(inputObject,0);
	}
	
	public ExtractTimeSequenceFromInput( StackSequenceInput inputObject, int seriesNum ) {
		super();
		this.inputObject = inputObject;
		this.seriesNum = seriesNum;
	}
	
	private TimeSequenceProvider doOperationWithException( ProgressReporter progressReporter ) throws RasterIOException {
		
		TimeSequence timeSeries;
		try {
			timeSeries = inputObject.createStackSequenceForSeries(seriesNum).doOperation(progressReporter);
		} catch (ExecuteException e) {
			throw new RasterIOException(e);
		}
		
		LazyEvaluationStore<TimeSequence> store = new LazyEvaluationStore<>(
			LogUtilities.createNullErrorReporter(),
			"extractTimeSequence"
		);
		
		try {
			store.add("input_stack", new IdentityOperation<>(timeSeries) );
			return new TimeSequenceProvider( store, inputObject.numFrames() );
		} catch (OperationFailedException e) {
			throw new RasterIOException(e);
		}
	}

	@Override
	protected TimeSequenceProvider execute( ProgressReporter progressReporter ) throws ExecuteException {
		try {
			return doOperationWithException( progressReporter );
		} catch (RasterIOException e) {
			throw new ExecuteException(e);
		}
	}
}