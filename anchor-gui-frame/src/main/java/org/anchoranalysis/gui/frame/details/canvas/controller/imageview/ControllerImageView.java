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

import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.*;

import java.awt.GraphicsConfiguration;

import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.details.canvas.ControllerZoom;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;

/** Controls a widget that contains a zoomed-image */
public class ControllerImageView {

	private ControllerSize size;
	private ControllerZoom zoom;
	
	// Leaves an extra margin to handle scrolling etc.
	private static int MARGIN_IMAGE = 60;
	
	public ControllerImageView(ControllerSize size, ControllerZoom zoom) {
		super();
		this.size = size;
		this.zoom = zoom;
	}
	
	
	/**
	 * Configures the frame to nicely match an image at a particular zoom level
	 * 
	 * with a width and height calculated as a percentage of screen-size
	 * 
	 * @param widthFractionScreen fraction-of-total-screen to use as width
	 * @param heightFractionScreen fraction-of-total-screen to use as height
	 * @param zoomWidthSubtract subtract this number of pixels from widthFractionScreen for calculating zoom width
	 * @param zoomHeightSubtract subtract this number of pixels from heightFractionScreen for calculating zoom height
	 */
	public void configureForImage(
		double widthFractionScreen,
		double heightFractionScreen,
		int zoomWidthSubtract,
		int zoomHeightSubtract,
		GraphicsConfiguration graphicsConfiguration,
		ImageDim imageSize
	) {
		Extent imageSizeZoom = SizeOfZoomedImage.apply(widthFractionScreen, heightFractionScreen,
			zoomWidthSubtract, zoomHeightSubtract, graphicsConfiguration, imageSize);
		
		Extent withMargin = addXY(imageSizeZoom, MARGIN_IMAGE, MARGIN_IMAGE);
		Extent withMarginAndPanel = addXY(withMargin, zoomWidthSubtract, zoomHeightSubtract); 
		
		configure(
			withMarginAndPanel,
			withMargin
		);
	}

	/**
	 * Configures the frame with a width and height calculated as a percentage of screen-size
	 * 
	 * @param widthFractionScreen fraction-of-total-screen to use as width
	 * @param heightFractionScreen fraction-of-total-screen to use as height
	 * @param zoomWidthSubtract subtract this number of pixels from widthFractionScreen for calculating zoom width
	 * @param zoomHeightSubtract subtract this number of pixels from heightFractionScreen for calculating zoom height
	 */
	public void configure(
		double widthFractionScreen,
		double heightFractionScreen,
		int zoomWidthSubtract,
		int zoomHeightSubtract,
		GraphicsConfiguration graphicsConfiguration
	) {
		Extent e = fractionScreenBounds(widthFractionScreen, heightFractionScreen, graphicsConfiguration);
		Extent eAdjusted = addXY(e, -zoomWidthSubtract, -zoomHeightSubtract);
		configure(e, eAdjusted);
	}
	
	public void setEnforceMinimumSizeAfterGuessZoom(boolean enforceMinimumSizeAfterGuessZoom) {
		zoom.setEnforceMinimumSizeAfterGuessZoom(enforceMinimumSizeAfterGuessZoom);
	}
		
	
	private void configure( Extent frame, Extent zoom ) {
		configure( frame.getX(), frame.getY(), zoom.getX(), zoom.getY() );
	}
	
	/**
	 * Configures the frame to have approximately a certain width and height, and the
	 *   image zoom-level to be configured in accordance to a certain zoomWidth and zoomHeight
	 * 
	 * @param width desired width of frame
	 * @param height desired height of frame
	 * @param zoomWidth expected width available for the image inside frame
	 * @param zoomHeight expected height available for the image inside frame
	 */
	private void configure(int width, int height, int zoomWidth, int zoomHeight) {
		
		size.configureSize(width, height);
		
		// We guess what size the image part will be in the final component
		zoom.setDefaultZoomSuggestor( new DefaultZoomSuggestor(zoomWidth, zoomHeight) );
	}
	
}
