package org.anchoranalysis.gui.frame.threaded.indexable;

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
import org.anchoranalysis.core.index.IndexBridge;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.IGenerateExtraDetail;
import org.anchoranalysis.gui.frame.multiraster.ThreadedIndexedDisplayStackSetter;
import org.anchoranalysis.gui.frame.threaded.stack.InternalFrameThreadedProvider;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.stack.DisplayStack;

/** A frame that allows for an indexable DisplayRaster */
public class InternalFrameThreadedIndexableRaster {

	private InternalFrameThreadedProvider delegate;
	
	private ThreadedIndexedDisplayStackSetter threadedProvider;
	
	public InternalFrameThreadedIndexableRaster( String frameName ) {
		delegate = new InternalFrameThreadedProvider( frameName, false );
	}

	public ControllerPopupMenu controllerPopupMenu() {
		return delegate.controllerPopupMenu();
	}

	public boolean addAdditionalDetails(IGenerateExtraDetail arg0) {
		return delegate.addAdditionalDetails(arg0);
	}

	public ISliderState init(
			IBoundedIndexContainer<DisplayStack> cntr,
			DefaultModuleState initialState,
			boolean includeFrameAdjusting,
			IRetrieveElements elementRetriever,
			VideoStatsModuleGlobalParams mpg
		) throws InitException {

		threadedProvider = setupProvider( cntr, mpg );
		
		ISliderState sliderState = delegate.init(
			threadedProvider,
			cntr,
			includeFrameAdjusting,
			initialState,
			elementRetriever,
			mpg
		);
		delegate.setIndexSliderVisible(true);
		return sliderState;
	}
	
	public IBackgroundSetter backgroundSetter() {
		return threadedProvider;
	}

	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return delegate.moduleCreator(sliderState);
	}

	public IRetrieveElements getElementRetriever() {
		return delegate.getElementRetriever();
	}

	public void setIndexSliderVisible(boolean visibility) {
		delegate.setIndexSliderVisible(visibility);
	}
	
	private static ThreadedIndexedDisplayStackSetter setupProvider(
			IBoundedIndexContainer<DisplayStack> cntr,
			VideoStatsModuleGlobalParams mpg
		) throws InitException {
			ThreadedIndexedDisplayStackSetter threadedProvider = new ThreadedIndexedDisplayStackSetter();
			threadedProvider.init(
				new IndexBridge<>(cntr),
				mpg.getThreadPool(),
				mpg.getLogErrorReporter().getErrorReporter()
			);
			return threadedProvider;
		}
}
