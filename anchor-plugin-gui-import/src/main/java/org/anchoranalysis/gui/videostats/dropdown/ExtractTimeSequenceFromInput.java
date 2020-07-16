/*-
 * #%L
 * anchor-plugin-gui-import
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
package org.anchoranalysis.gui.videostats.dropdown;


import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.log.LogUtilities;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.plugin.io.bean.input.stack.StackSequenceInput;

public class ExtractTimeSequenceFromInput extends CachedOperationWithProgressReporter<TimeSequenceProvider,CreateException> {

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
	
	private TimeSequenceProvider doOperationWithException( ProgressReporter progressReporter ) throws CreateException {
		
		try {
			TimeSequence timeSeries = inputObject
				.createStackSequenceForSeries(seriesNum)
				.doOperation(progressReporter);
			
			LazyEvaluationStore<TimeSequence> store = new LazyEvaluationStore<>(
				LogUtilities.createNullErrorReporter(),
				"extractTimeSequence"
			);
			
			store.add(
				"input_stack",
				new IdentityOperation<>(timeSeries)
			);
			
			return new TimeSequenceProvider(
				store,
				inputObject.numFrames()
			);
		} catch (RasterIOException | OperationFailedException e) {
			throw new CreateException(e);
		}
	}

	@Override
	protected TimeSequenceProvider execute( ProgressReporter progressReporter ) throws CreateException {
		return doOperationWithException( progressReporter );
	}
}
