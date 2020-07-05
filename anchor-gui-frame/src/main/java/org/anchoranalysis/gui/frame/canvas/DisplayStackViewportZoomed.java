package org.anchoranalysis.gui.frame.canvas;

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


import java.awt.image.BufferedImage;
import java.util.Optional;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * A DisplayStackViewport zoomed by the factor zoomScale
 * 
 * Values here are in terms of a scaled-space (the space of delegate * zoomFactor)
 * 
 * @author Owen Feehan
 *
 */
class DisplayStackViewportZoomed {

	private DisplayStackViewport delegate;
	private ZoomScale zoomScale;
	
	public DisplayStackViewportZoomed() {
		super();
		this.delegate = new DisplayStackViewport();
		this.zoomScale = new ZoomScale();
	}

	// Updates the view to bind to a new location, and returns a BufferedImage if has changed
	//   or null otherwise
	// The bounding box should refer to the Scaled space
	public BufferedImage updateView(BoundingBox bbox) throws OperationFailedException {
		BoundingBox bboxScaled = bbox.scale(
			new ScaleFactor(zoomScale.getScaleInv())
		);
		
		// Scaling seemingly can produce a bbox that is slightly too-big
		bboxScaled = bboxScaled.clipTo( delegate.getDisplayStackEntireImage().getDimensions().getExtnt() );
		assert( delegate.getDisplayStackEntireImage().getDimensions().contains(bboxScaled));
		return delegate.updateView(bboxScaled, zoomScale);
	}
	
	public BoundingBox createBoxForShiftedView(Point2i shift, Extent canvasExtnt) {
		
		Point2i shiftImg = zoomScale.removeScale(shift);
		Extent canvasExtntImg = zoomScale.removeScale(canvasExtnt);
		
		BoundingBox shiftedBox = delegate
			.createBoxForShiftedView(shiftImg, canvasExtntImg)
			.scale(
				new ScaleFactor(zoomScale.getScale())
			);
		
		assert( shiftedBox.cornerMin().getX() >= 0 );
		assert( shiftedBox.cornerMin().getY() >= 0 );
		
		return shiftedBox;
	}
	

	private int cnvrtCanvasXToImage( int val, ZoomScale zs ) {
		return zs.removeScale(val) + delegate.getBBox().cornerMin().getX();
	}
	
	private int cnvrtCanvasYToImage( int val, ZoomScale zs ) {
		return zs.removeScale(val) + delegate.getBBox().cornerMin().getY();
	}
	
	
	public int cnvrtImageXToCanvas( int val ) {
		return zoomScale.applyScale(val-delegate.getBBox().cornerMin().getX());
	}
	
	public int cnvrtImageYToCanvas( int val ) {
		return zoomScale.applyScale(val-delegate.getBBox().cornerMin().getY());
	}
		
	public int cnvrtCanvasXToImage( int val ) {
		return zoomScale.removeScale(val) + delegate.getBBox().cornerMin().getX();
	}
	
	public int cnvrtCanvasYToImage( int val ) {
		return zoomScale.removeScale(val) + delegate.getBBox().cornerMin().getY();
	}
	
	
	
	public void setDisplayStackEntireImage(BoundOverlayedDisplayStack displayStack) throws SetOperationFailedException {
		delegate.setDisplayStackEntireImage(displayStack);
	}

	public ImageDimensions getDimensionsEntire() {
		return delegate.getDimensionsEntire();
	}

	public ZoomScale getZoomScale() {
		return zoomScale;
	}

	public void setZoomScale(ZoomScale zoomScale) {
		this.zoomScale = zoomScale;
	}
	
	public ImageDimensions createDimensionsEntireScaled() {
		return getDimensionsEntire().scaleXYBy(
			new ScaleFactor(zoomScale.getScale())
		);
	}

	public ImageResolution getRes() {
		return delegate.getDimensionsEntire().getRes();
	}

	public Point2i calcNewCrnrPosToMaintainMousePoint(Point2i mousePoint, ZoomScale zoomScaleOld ) {

		// Mouse point is already in image-cordinates
		Point2i imgPointOld = new Point2i(
			cnvrtCanvasXToImage(mousePoint.getX(), zoomScaleOld),
			cnvrtCanvasYToImage(mousePoint.getY(), zoomScaleOld)
		);
		
		// We want the mousePoint at the new scale, to be on the same img point
		Point2i imgPointNewGlobal = zoomScale.applyScale(imgPointOld);
		
		// Corner point
		Point2i crnrPnt = new Point2i();
		crnrPnt.setX( imgPointNewGlobal.getX() - mousePoint.getX() );
		crnrPnt.setY( imgPointNewGlobal.getY() - mousePoint.getY() );
		
		// But if they are less than 0, then we need to adjust
		crnrPnt.setX( Math.max( crnrPnt.getX(), 0 ) );
		crnrPnt.setY( Math.max( crnrPnt.getY(), 0 ) );
		return crnrPnt;
	}
	
	public Point2i calcNewCrnrPosAfterChangeInZoom(Extent canvasExtntOld, ZoomScale zoomScaleOld,
			Extent canvasExtntNew, Point2i scrollValImage) {
		
		Extent canvasExtntNewImage = zoomScale.removeScale(canvasExtntNew);
		Extent canvasExtntOldImage = zoomScaleOld.removeScale( canvasExtntOld );
		
		Point2i scaleFree = delegate.calcNewCrnrPosAfterChangeInViewSize(canvasExtntOldImage,
				canvasExtntNewImage, scrollValImage);
		return zoomScale.applyScale(scaleFree); 
	}

	public Point2i removeScale(Point2i pnt) {
		return zoomScale.removeScale(pnt);
	}

	public BoundingBox getBBox() {
		return zoomScale.applyScale( delegate.getBBox() );
	}
	
	// If the image point x,y is contained within the canvas
	public boolean canvasContainsAbs( int x, int y, int z ) {
		return delegate.canvasContainsAbs(x,y, z);
	}
	
	public String intensityStrAtAbs( int x, int y, int z ) {
		return delegate.intensityStrAtAbs(x, y, z);
	}
	
	// empty() means it cannot be determined
	public Optional<VoxelDataType> associatedDataType() {
		return delegate.associatedDataType();
	}

	public BoundOverlayedDisplayStack getDisplayStackEntireImage() {
		return delegate.getDisplayStackEntireImage();
	}

	public DisplayStackViewport getUnzoomed() {
		return delegate;
	}

}
