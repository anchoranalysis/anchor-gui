package org.anchoranalysis.gui.frame.canvas;

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

import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;

class DisplayStackViewportUtilities {


	// Ensures a particular value is in the acceptable X range
	private static int clipToImage( int val, int entireExtnt, int canvasWidth ) {
		
		if (val<0) {
			return 0;
		}
	
		int furthestXVal = Math.max(entireExtnt - canvasWidth,0); 
		if (val>furthestXVal) {
			return furthestXVal;
		}
		
		return val;
	}
	
	public static Point2i clipToImage( Point2i val, Extent canvasExtnt, ImageDimensions sdImage ) {
		Point2i out = new Point2i();
		out.setX( clipToImage(val.getX(), sdImage.getX(), canvasExtnt.getX()) );
		out.setY( clipToImage(val.getY(), sdImage.getY(), canvasExtnt.getY()) );
		return out;
	}
}
