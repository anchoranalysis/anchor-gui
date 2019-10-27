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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;

public class OperationCreateBackgroundSet extends CachedOperationWithProgressReporter<BackgroundSet> {

	private OperationWithProgressReporter<TimeSequenceProvider> namedImgStackCollection;
	
	public OperationCreateBackgroundSet( INamedProvider<Stack> namedProvider ) {
		this(
			progressReporter -> new TimeSequenceProvider(
				new WrapStackAsTimeSequence(namedProvider),
				1
			)
		);
	}
	
	public OperationCreateBackgroundSet( OperationWithProgressReporter<TimeSequenceProvider> namedImgStackCollection ) {
		super();
		this.namedImgStackCollection = namedImgStackCollection;
	}

	@Override
	protected BackgroundSet execute( ProgressReporter progressReporter ) throws ExecuteException {
		try {
			INamedProvider<TimeSequence> stacks = namedImgStackCollection.doOperation( progressReporter ).sequence();

			BackgroundSet backgroundSet = BackgroundSetFactory.createBackgroundSet(
				stacks,
				ProgressReporterNull.get()
			);
			
			return backgroundSet;
		} catch (CreateException e) {
			throw new ExecuteException(e);
		}
	}
}