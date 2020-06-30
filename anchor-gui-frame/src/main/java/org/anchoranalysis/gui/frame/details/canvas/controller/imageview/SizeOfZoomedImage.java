package org.anchoranalysis.gui.frame.details.canvas.controller.imageview;

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

import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.addXY;
import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.fractionScreenBounds;

import java.awt.GraphicsConfiguration;

import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;

/**
 * The size of an image after it has been zoomed to fit in a certain % of the screen-size
 * 
 * @author FEEHANO
 *
 */
class SizeOfZoomedImage {
	
	public static Extent apply(
		double widthFractionScreen,
		double heightFractionScreen,
		int zoomWidthSubtract,
		int zoomHeightSubtract,
		GraphicsConfiguration graphicsConfiguration,
		ImageDimensions imageSize	
	) {
		Extent e = fractionScreenBounds(widthFractionScreen, heightFractionScreen, graphicsConfiguration);
		Extent eAdjusted = addXY(e, -zoomWidthSubtract, -zoomHeightSubtract);
		return sizeWithinBounds(imageSize, eAdjusted);
	}
	
	/** The size of a zoomed-image after being zoomed to fit within bounds */ 
	private static Extent sizeWithinBounds( ImageDimensions imageSize, Extent maxBounds ) {
		DefaultZoomSuggestor zoomSugg = new DefaultZoomSuggestor( maxBounds.getX(), maxBounds.getY() );
		ZoomScale zs = zoomSugg.suggestDefaultZoomFor(imageSize);
		
		// The size of the image after it has been zoomed
		return zs.applyScale(imageSize.getExtnt());
	}
}
