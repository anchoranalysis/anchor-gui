package org.anchoranalysis.gui.io.loader.manifest.finder;

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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.gui.finder.FinderRasterSingleChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReaderUtilities;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderSingleFile;

public abstract class FinderRasterStack extends FinderSingleFile implements FinderRasterSingleChnl, BackgroundStackCntr {

	private Stack result;
	
	private RasterReader rasterReader;

	public FinderRasterStack( RasterReader rasterReader, ErrorReporter errorReporter ) {
		super(errorReporter);
		this.rasterReader = rasterReader;
	}

	private Stack createStack( FileWrite fileWrite ) throws GetOperationFailedException, RasterIOException {
		
		if (fileWrite==null) {
			return null;
		}
		
		// Assume single series, single channel
		return RasterReaderUtilities.openStackFromPath(
			rasterReader,
			fileWrite.calcPath()
		);
	}
	
	public Stack get() throws GetOperationFailedException {
		assert(exists());
		if (result==null) {
			try {
				result = createStack( getFoundFile() );
			} catch (RasterIOException e) {
				throw new GetOperationFailedException(e);
			}
		}
		return result;
	}
	

	@Override
	public Channel getFirstChnl() throws GetOperationFailedException {
		return get().getChnl(0);
	}
	
	@Override
	public IBoundedIndexContainer<DisplayStack> backgroundStackCntr() throws GetOperationFailedException {
		Stack resultNormalized = get().duplicate();
		
		try {
			DisplayStack bgStack = DisplayStack.create(resultNormalized );
			return new SingleContainer<>( bgStack,0,true);
		} catch (CreateException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	
	
	
	
	@Override
	public int getNumChnl() throws GetOperationFailedException {
		return get().getNumChnl();
	}

}
