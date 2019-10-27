package org.anchoranalysis.gui.videostats.dropdown.common;

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

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.progress.IdentityOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.dropdown.AdderAppendNRGStack;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.OperationCreateBackgroundSet;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;

/** 
 * NRGStack and background together
 * 
 * A background will always exist
 * 
 * An nrg-stack may not be defined, in which case a *guess* can be made, if necessary.
 * 
 */
public class NRGBackground {

	private OperationWithProgressReporter<BackgroundSet> opBackgroundSet;
	private Operation<NRGStackWithParams> opNrgStack;
	private OperationWithProgressReporter<Integer> opNumFrames;
	
	private NRGBackground(
		OperationWithProgressReporter<BackgroundSet> opBackgroundSet,
		Operation<NRGStackWithParams> opNrgStack,
		OperationWithProgressReporter<Integer> opNumFrames
	) {
		super();
		this.opBackgroundSet = opBackgroundSet;
		this.opNrgStack = opNrgStack;
		this.opNumFrames = opNumFrames;
	}
	
	private NRGBackground(
		OperationWithProgressReporter<BackgroundSet> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams> opNrgStack,
		OperationWithProgressReporter<Integer> opNumFrames
	) {
		super();
		this.opBackgroundSet = opBackgroundSet;
		this.opNrgStack = removeProgressReporter( opNrgStack );
		this.opNumFrames = opNumFrames;
		assert( opNumFrames!= null );
	}
	
	public static NRGBackground createFromBackground(
		OperationWithProgressReporter<BackgroundSet> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams> opNrgStack		
	) {
		return new NRGBackground(
			opBackgroundSet,
			opNrgStack,
			new IdentityOperationWithProgressReporter<>(1)
		);
	}
	
	public static NRGBackground createStack(
		OperationWithProgressReporter<INamedProvider<Stack>> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams> opNrgStack
	) {
		OperationWithProgressReporter<TimeSequenceProvider> opConvert = progressReporter -> { 
			return new TimeSequenceProvider(
				new WrapStackAsTimeSequence( opBackgroundSet.doOperation(progressReporter) ),
				1
			);
		};
		return createStackSequence( opConvert, opNrgStack );
	}
	
	public static NRGBackground createStackSequence(
		OperationWithProgressReporter<TimeSequenceProvider> opBackgroundSet,
		OperationWithProgressReporter<NRGStackWithParams> opNrgStack
	) {
		return new NRGBackground(
			convertProvider(opBackgroundSet),
			opNrgStack,
			progress -> opBackgroundSet.doOperation(progress).getNumFrames()
		); 
	}

	public OperationWithProgressReporter<BackgroundSet> getBackgroundSet() {
		return opBackgroundSet;
	}
	
	public IAddVideoStatsModule addNrgStackToAdder( IAddVideoStatsModule adder ) {
		return new AdderAppendNRGStack(adder, convertToGetter() );
	}
	
	// Assumes the number of frames does not change
	public NRGBackground copyChangeOp( OperationWithProgressReporter<BackgroundSet> opBackgroundSetNew ) {
		return new NRGBackground(opBackgroundSetNew, opNrgStack, opNumFrames);
	}
	
	private INRGStackGetter convertToGetter() {
		return () -> {
			try {
				return opNrgStack.doOperation();
			} catch (ExecuteException e) {
				throw new GetOperationFailedException(e);
			}
		};
	}
	
	private static Operation<NRGStackWithParams> removeProgressReporter( OperationWithProgressReporter<NRGStackWithParams> in ) {
		return () -> {
			return in.doOperation( ProgressReporterNull.get() );
		};
	}
	
	private static OperationWithProgressReporter<BackgroundSet> convertProvider( OperationWithProgressReporter<TimeSequenceProvider> in) {
		return new OperationCreateBackgroundSet(in);
	}

	public Operation<NRGStackWithParams> getNrgStack() {
		return opNrgStack;
	}
	
	public int numFrames() throws OperationFailedException {
		try {
			return opNumFrames.doOperation( ProgressReporterNull.get() );
		} catch (ExecuteException e) {
			throw new OperationFailedException(e);
		}
	}
	
	
	public String arbitraryBackgroundStackName() throws InitException {
		try {
			return getBackgroundSet().doOperation( ProgressReporterNull.get() )
				.names()
				.iterator()
				.next();
			
		} catch (ExecuteException e) {
			throw new InitException(e);
		}			
	}
}
