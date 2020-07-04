package org.anchoranalysis.gui.io.loader.manifest.finder.probmap;

import org.anchoranalysis.core.error.CreateException;

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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.gui.finder.FinderRasterSingleChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.finder.Finder;

public class FinderProbMap implements BackgroundStackCntr, FinderRasterSingleChnl, Finder{

	private final FinderProbMapSingleRaster singleRaster;

	private final FinderProbMapRasterSeries rasterSeries;
	
	private final String displayName;
	
	public FinderProbMap(RasterReader rasterReader, String singleRasterOutputName, ErrorReporter errorReporter ) {
		singleRaster = new FinderProbMapSingleRaster(rasterReader, singleRasterOutputName, errorReporter );
		rasterSeries = new FinderProbMapRasterSeries(rasterReader, singleRasterOutputName);
		displayName = singleRasterOutputName;
	}

	@Override
	public boolean doFind(ManifestRecorder manifestRecorder) {

		// We find both in case we do a singleChnl() call later on
		boolean series = rasterSeries.doFind(manifestRecorder);

		if (series) {
			return true;
		}
		
		boolean single = singleRaster.doFind(manifestRecorder);
		
		if (single) {
			return true;
		}
	
		return false;
	}

	@Override
	public boolean exists() {
		return singleRaster.exists() || rasterSeries.exists();
	}

	public boolean isRasterSeries() {
		return rasterSeries.exists();
	}
	
	public boolean isSingle() {
		return !isRasterSeries();
	}
	
	public IBoundedIndexContainer<Stack> getRasterSeries() throws GetOperationFailedException {
		return rasterSeries.get();
	}
	
	// Returns a single channel the probMap, o series allowed
	public Channel singleChnl() throws GetOperationFailedException {
		if (singleRaster.exists()) {
			return singleRaster.get();
		}
		if (isRasterSeries()) {
			return rasterSeries.get().get( rasterSeries.get().previousEqualIndex(0) ).getChnl(0);
		}
		throw new AnchorImpossibleSituationException();
	}
	

	@Override
	public IBoundedIndexContainer<DisplayStack> backgroundStackCntr() throws GetOperationFailedException {

		assert(exists());
		
		try {
			if (isSingle()) {
				Channel chnl = singleRaster.get();
				
				Stack stack = new Stack();
				stack.addChnl(chnl);
				stack.addBlankChnl();
				stack.addBlankChnl();
				
				DisplayStack bgStack = DisplayStack.create(stack );
				return new SingleContainer<>(bgStack, 0,true);
			} else {
				// Otherwise we take the container from the RasterSeries
				return new BoundedIndexContainerBridgeWithoutIndex<>(
					rasterSeries.get(),
					new BackgroundStackBridge()
				);
			}
		} catch (OperationFailedException | CreateException | IncorrectImageSizeException e) {
			throw new GetOperationFailedException(e);
		}
	}
		
	private static class BackgroundStackBridge implements FunctionWithException<Stack, DisplayStack,CreateException> {

		@Override
		public DisplayStack apply(Stack sourceObject) throws CreateException {
			return DisplayStack.create(sourceObject );
		}
	}

	public FinderProbMapSingleRaster getFinderSingleRaster() {
		return singleRaster;
	}
	
	@Override
	public Channel getFirstChnl() throws GetOperationFailedException {
		return singleChnl();
	}

	@Override
	public int getNumChnl() throws GetOperationFailedException {
		return 1;
	}

	public String getDisplayName() {
		return displayName;
	}
}

