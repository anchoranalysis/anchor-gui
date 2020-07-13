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


import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.videostats.dropdown.addoverlays.AdderAddOverlaysWithStack;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackgroundAdder;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.stack.DisplayStack;

public class OperationCreateBackgroundSetWithAdder extends CachedOperationWithProgressReporter<BackgroundSetWithAdder,OperationFailedException> {

	private IAddVideoStatsModule parentAdder;
	private InteractiveThreadPool threadPool;
	private ErrorReporter errorReporter;
	
	
	private NRGBackground nrgBackground;
	private NRGBackgroundAdder<OperationFailedException> nrgBackgroundNew;
	
	private CachedOperationWithProgressReporter<IAddVideoStatsModule,OperationFailedException> operationIAddVideoStatsModule = new WrapOperationWithProgressReporterAsCached<>(
		progressReporter -> {
			BackgroundSetWithAdder bwsa = OperationCreateBackgroundSetWithAdder.this.doOperation( progressReporter );
			return bwsa.getAdder();	
		}
	);
	
	private CachedOperationWithProgressReporter<BackgroundSet,GetOperationFailedException> operationBackgroundSet = new WrapOperationWithProgressReporterAsCached<>(
		progressReporter -> {
			try {
				BackgroundSetWithAdder bwsa = OperationCreateBackgroundSetWithAdder.this.doOperation( progressReporter );
				return bwsa.getBackgroundSet();
			} catch (OperationFailedException e) {
				throw new GetOperationFailedException(e);
			}
		}
	);
	
	public OperationCreateBackgroundSetWithAdder(
		NRGBackground nrgBackground,
		IAddVideoStatsModule parentAdder,
		InteractiveThreadPool threadPool,
		ErrorReporter errorReporter
	) {
		super();
		this.nrgBackground = nrgBackground;
		this.parentAdder = parentAdder;
		this.threadPool = threadPool;
		this.errorReporter = errorReporter;
		
		// A new nrgBackground that includes the changed operation for the background
		this.nrgBackgroundNew = new NRGBackgroundAdder<>(
			nrgBackground.copyChangeOp(operationBackgroundSet),
			operationIAddVideoStatsModule
		);
	}
	
	private static FunctionWithException<Integer,DisplayStack,GetOperationFailedException> initialBackground( BackgroundSet backgroundSet ) throws GetOperationFailedException {
		
		if (backgroundSet.names().contains(ImgStackIdentifiers.INPUT_IMAGE)) {
			return backgroundSet.stackCntr( ImgStackIdentifiers.INPUT_IMAGE );
		} else {
			String first = backgroundSet.names().iterator().next();
			return backgroundSet.stackCntr( first );
		}
	}

	private static void assignDefaultSliceForStack( IAddVideoStatsModule childAdder, DisplayStack stack ) {
		
		IPropertyValueSendable<Integer> sliceNumSetter = childAdder.getSubgroup().getDefaultModuleState().getLinkStateManager().getSendableSliceNum();
		if (sliceNumSetter!=null) {
			sliceNumSetter.setPropertyValue(stack.getDimensions().getZ()/2, false);
		}
	}
	
	@Override
	protected BackgroundSetWithAdder execute( ProgressReporter progressReporter ) throws OperationFailedException {
		BackgroundSetWithAdder bwsa = new BackgroundSetWithAdder();
		
		BackgroundSet backgroundSet;
		try {
			backgroundSet = nrgBackground.getBackgroundSet().doOperation(progressReporter);
		} catch (GetOperationFailedException e1) {
			throw new OperationFailedException(e1);
		}
		
		bwsa.setBackgroundSet(backgroundSet);
		
		IAddVideoStatsModule childAdder = parentAdder.createChild();
		childAdder = new AdderAddOverlaysWithStack( childAdder, threadPool, errorReporter );
		
		childAdder = nrgBackground.addNrgStackToAdder(childAdder);
		
		FunctionWithException<Integer,DisplayStack,GetOperationFailedException> initialBackground;
		try {
			initialBackground = initialBackground(backgroundSet);
		} catch (GetOperationFailedException e) {
			throw new OperationFailedException("Cannot set defaultModuleState background: " + e);
		}
		
		// TODO For now we assume there is always an index 0 available as a minimum
		DisplayStack initialStack;
		try {
			initialStack = initialBackground.apply(0);
		} catch (GetOperationFailedException e) {
			throw new OperationFailedException("Cannot set defaultModuleState background: " + e);
		}
		
		// TODO is this the right place?
		// Sets an appropriate default slice in the middle
		assignDefaultSliceForStack(childAdder, initialStack );
		
		bwsa.setAdder(childAdder);
		
		childAdder.getSubgroup().getDefaultModuleState().getLinkStateManager().setBackground( initialBackground );

		return bwsa;
	}
			
	public OperationWithProgressReporter<IAddVideoStatsModule,OperationFailedException> operationAdder() {
		return operationIAddVideoStatsModule;
	}
	
	public NRGBackgroundAdder<OperationFailedException> nrgBackground() {
		return nrgBackgroundNew;
	}
}