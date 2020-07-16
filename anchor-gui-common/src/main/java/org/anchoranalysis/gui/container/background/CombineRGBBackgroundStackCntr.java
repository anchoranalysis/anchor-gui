/*-
 * #%L
 * anchor-gui-common
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.container.background;


import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;

public class CombineRGBBackgroundStackCntr {

	private BackgroundStackCntr red;
	private BackgroundStackCntr green;
	private BackgroundStackCntr blue;
	
	public BackgroundStackCntr create() throws GetOperationFailedException, InitException {
		
		final CombineRGBBoundedIndexContainer combinedCntr = new CombineRGBBoundedIndexContainer();
		
		assert( red!=null || green!=null || blue!=null );
		
		if (red!=null) {
			combinedCntr.setRed(red.backgroundStackCntr());
		}

		if (green!=null) {
			combinedCntr.setGreen(green.backgroundStackCntr());
		}
		
		if (blue!=null) {
			combinedCntr.setBlue(blue.backgroundStackCntr());
		}
		
		combinedCntr.init();
		
		return new SingleBackgroundStackCntr(combinedCntr);
	}

	public BackgroundStackCntr getRed() {
		return red;
	}

	public void setRed(BackgroundStackCntr red) {
		this.red = red;
	}

	public BackgroundStackCntr getGreen() {
		return green;
	}

	public void setGreen(BackgroundStackCntr green) {
		this.green = green;
	}

	public BackgroundStackCntr getBlue() {
		return blue;
	}

	public void setBlue(BackgroundStackCntr blue) {
		this.blue = blue;
	}
	
	
}
