package org.anchoranalysis.gui.videostats.dropdown.common;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class GuessNRGStackFromStacks implements OperationWithProgressReporter<NRGStackWithParams,OperationFailedException> {

	private OperationWithProgressReporter<TimeSequenceProvider,CreateException> opBackgroundSet;
	
	public GuessNRGStackFromStacks(OperationWithProgressReporter<TimeSequenceProvider,CreateException> opBackgroundSet) {
		super();
		this.opBackgroundSet = opBackgroundSet;
	}
	
	@Override
	public NRGStackWithParams doOperation(ProgressReporter progressReporter) throws OperationFailedException {
		// If a time sequence, assume nrg stack is always t=0
		Stack stack = selectArbitraryItem(opBackgroundSet).get(0);
		
		return new NRGStackWithParams(stack);
	}
	
	private static TimeSequence selectArbitraryItem( OperationWithProgressReporter<TimeSequenceProvider,CreateException> opBackgroundSet ) throws OperationFailedException {
		try {
			INamedProvider<TimeSequence> stacks = opBackgroundSet.doOperation( ProgressReporterNull.get() ).sequence();
			String arbitraryKey = stacks.keys().iterator().next();
			
			// If a time sequence, assume nrg stack is always t=0
			return stacks.getException(arbitraryKey);
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}

}
