package org.anchoranalysis.gui.frame.multiraster;



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


import java.util.List;

import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainerFromList;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.threaded.indexable.InternalFrameThreadedIndexableRaster;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.stack.DisplayStack;

/* Many rasters shown in sequence */
public class InternalFrameMultiRaster {

	private InternalFrameThreadedIndexableRaster delegate;
	
	public InternalFrameMultiRaster( String frameName ) {
		delegate = new InternalFrameThreadedIndexableRaster( frameName );
	}
	
	public ISliderState init(
			List<NamedRasterSet> list,
			DefaultModuleState initialState,
			IRetrieveElements elementRetriever,
			VideoStatsModuleGlobalParams mpg
		) throws InitException {

		assert( mpg.getLogErrorReporter()!=null );
		
		BoundedIndexContainerBridgeWithoutIndex<NamedRasterSet,DisplayStack> bridge = new BoundedIndexContainerBridgeWithoutIndex<>(
			new BoundedIndexContainerFromList<>(list),
			InternalFrameMultiRaster::convertToDisplayStack
		);
		
		ISliderState sliderState = delegate.init(
			bridge,
			initialState,
			false,
			elementRetriever,
			mpg
		);
			
		delegate.addAdditionalDetails(
			index -> String.format(
				"id=%s",
				list.get(index).getName()
			)
		);
		
		AddBackgroundPopup.apply(
			delegate.controllerPopupMenu(),
			delegate.backgroundSetter(),
			list,
			sliderState,
			mpg
		);
		
		return sliderState;
	}
	
	private static DisplayStack convertToDisplayStack( NamedRasterSet set ) throws BridgeElementException {
		try {
			return ConvertToDisplayStack.apply(set);
		} catch (OperationFailedException e) {
			throw new BridgeElementException(e);
		}
	}
	
	public IRetrieveElements getElementRetriever() {
		return delegate.getElementRetriever();
	}

	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return delegate.moduleCreator(sliderState);
	}
}
