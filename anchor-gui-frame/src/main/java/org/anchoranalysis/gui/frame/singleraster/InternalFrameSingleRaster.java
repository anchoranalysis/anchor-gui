package org.anchoranalysis.gui.frame.singleraster;

/*-
 * #%L
 * anchor-gui-frame
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



import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.container.IntegerSequenceContaner;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.threaded.indexable.InternalFrameThreadedIndexableRaster;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.stack.DisplayStack;

public class InternalFrameSingleRaster {

	private InternalFrameThreadedIndexableRaster delegate;
	
	private ControllerPopupMenuWithBackground controller;
	
	public InternalFrameSingleRaster( String frameName ) {
		delegate = new InternalFrameThreadedIndexableRaster(frameName);
	}
	
	public ISliderState init(
		int numFrames,
		DefaultModuleState initialState,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		
		// Create a sequence from 0 to numFrames -1, and map to our bridge
		BoundedIndexContainerBridgeWithoutIndex<Integer, DisplayStack> bridge = new BoundedIndexContainerBridgeWithoutIndex<>(
			new IntegerSequenceContaner(numFrames),
			initialState.getLinkState().getBackground()
		);
		
		ISliderState sliderState = delegate.init(
			bridge,
			initialState,
			true,
			delegate.getElementRetriever(),
			mpg
		);
		
		// Let's switch off the index-bar if we only have a single frame
		if (numFrames==1) {
			delegate.setIndexSliderVisible(false);
		}
		
		controller = new ControllerPopupMenuWithBackground(
			delegate.controllerPopupMenu(),
			delegate.backgroundSetter()
		);
		
		return sliderState;
	}

	public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
		return controller;
	}

	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return delegate.moduleCreator(sliderState);
	}
}
