package org.anchoranalysis.gui.container.background;

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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;

public class CombineRGBBoundedIndexContainer implements IBoundedIndexContainer<DisplayStack> {

	private IBoundedIndexContainer<DisplayStack> red;
	private IBoundedIndexContainer<DisplayStack> blue;
	private IBoundedIndexContainer<DisplayStack> green;
	
	private int min;
	private int max;
	
	private ImageDim dim;
	
	// We assume we will never have an index above this number
	private static int MAX_NEVER_REACHED = 1000000;
	
	private void setDimensionsIfNeeded( IBoundedIndexContainer<DisplayStack> cntr ) throws GetOperationFailedException {
		if (cntr!=null && dim==null) {
			dim = cntr.get( cntr.getMinimumIndex()).getDimensions();
		}
	}
	
	public void init() throws InitException {
		
		assert( red!=null || green!=null || blue!=null );
		
		// We calculate minimum of the three channels
		try {
		
			min = MAX_NEVER_REACHED;
			max = 0;
			
			if (red!=null) {
				min = Math.min(min, red.getMinimumIndex());
				max = Math.max(max, red.getMaximumIndex());
				setDimensionsIfNeeded(red);
			} 
	
			if (green!=null) {
				min = Math.min(min, green.getMinimumIndex());
				max = Math.max(max, green.getMaximumIndex());
				setDimensionsIfNeeded(green);
			}
			
			if (blue!=null) {
				min = Math.min(min, blue.getMinimumIndex());
				max = Math.max(max, blue.getMaximumIndex());
				setDimensionsIfNeeded(blue);
			}
	
		} catch (GetOperationFailedException e) {
			throw new InitException(e);
		}
	}
	
	// GETTERS AND SETTERS
	
	public IBoundedIndexContainer<DisplayStack> getRed() {
		return red;
	}

	public void setRed(IBoundedIndexContainer<DisplayStack> red) {
		this.red = red;
	}

	public IBoundedIndexContainer<DisplayStack> getBlue() {
		return blue;
	}

	public void setBlue(IBoundedIndexContainer<DisplayStack> blue) {
		this.blue = blue;
	}

	public IBoundedIndexContainer<DisplayStack> getGreen() {
		return green;
	}

	public void setGreen(IBoundedIndexContainer<DisplayStack> green) {
		this.green = green;
	}



	@Override
	public void addBoundChangeListener(BoundChangeListener cl) {
		// WE ASSUME BOUNDS don't change		
	}



	@Override
	public int nextIndex(int index) {
		
		// We find the previousEqual of each cntr, and take the highest
		int indexOut = MAX_NEVER_REACHED;
		
		if( red!=null) {
			indexOut = Math.min( indexOut, red.nextIndex(index) );
		}
		
		if( green!=null) {
			indexOut = Math.min( indexOut, green.nextIndex(index) );
		}
		
		if( blue!=null) {
			indexOut = Math.min( indexOut, blue.nextIndex(index) );
		}
		
		return indexOut;
	}



	@Override
	public int previousIndex(int index) {

		// We find the previousEqual of each cntr, and take the highest
		int indexOut = 0;
		
		if( red!=null) {
			indexOut = Math.max( indexOut, red.previousIndex(index) );
		}
		
		if( green!=null) {
			indexOut = Math.max( indexOut, green.previousIndex(index) );
		}
		
		if( blue!=null) {
			indexOut = Math.max( indexOut, blue.previousIndex(index) );
		}
		
		return indexOut;
	}



	@Override
	public int previousEqualIndex(int index) {
		
		// We find the previousEqual of each cntr, and take the highest
		int indexOut = 0;
		
		if( red!=null) {
			indexOut = Math.max( indexOut, red.previousEqualIndex(index) );
		}
		
		if( green!=null) {
			indexOut = Math.max( indexOut, green.previousEqualIndex(index) );
		}
		
		if( blue!=null) {
			indexOut = Math.max( indexOut, blue.previousEqualIndex(index) );
		}
		
		return indexOut;
	}



	@Override
	public int getMinimumIndex() {
		return min;
	}



	@Override
	public int getMaximumIndex() {
		return max;
	}


	private void addChnlToStack(Stack stackNew, IBoundedIndexContainer<DisplayStack> cntr, int index ) throws GetOperationFailedException {
		
		try {
			if (cntr!=null) {
				stackNew.addChnl( cntr.get( cntr.previousEqualIndex(index) ).createChnl(0, false) );
			} else {
				// TODO, why do we create an empty initialised here
				assert(dim!=null);
				Channel chnlNew = ChannelFactory.instance().createEmptyInitialised(dim, VoxelDataTypeUnsignedByte.instance);
				stackNew.addChnl( chnlNew );
			}
		} catch (IncorrectImageSizeException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	@Override
	public DisplayStack get(int index) throws GetOperationFailedException {
		
		// We create a new image composed of the first channel from each of the underlying containers
		Stack stackNew = new Stack();
		
		addChnlToStack( stackNew, red, index );
		addChnlToStack( stackNew, green, index );
		addChnlToStack( stackNew, blue, index );
		
		try {
			return DisplayStack.create( stackNew );
		} catch (CreateException e) {
			throw new GetOperationFailedException(e);
		}
	}

}
