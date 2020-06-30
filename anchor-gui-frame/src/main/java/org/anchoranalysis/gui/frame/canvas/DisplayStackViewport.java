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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

// Shows a certain amount of a stack at any given time
class DisplayStackViewport {

	private BoundingBox bboxViewport = null;
	private ZoomScale zoomScale;

	
	private BoundOverlayedDisplayStack displayStackEntireImage;
	
	/**
	 * The currently shown DisplayStack (retrieved from regionInterpolator)
	 */
	private BufferedImage displayStackCurrentlyShown;
	
	/**
	 * How we extract bounding-boxes of pixels
	 */
	private RegionExtracter regionExtracter;
	
	
		
	public void setDisplayStackEntireImage( BoundOverlayedDisplayStack displayStack ) throws SetOperationFailedException {
		this.displayStackEntireImage = displayStack;
		
		// We get a new regionExtracter as the displaystack has changed
		regionExtracter = displayStack.createRegionExtracter();
	}
	
	public ImageDimensions getDimensionsEntire() {
		return displayStackEntireImage.getDimensions();
	}
	
	public BoundingBox getBBox() {
		return bboxViewport;
	}
	
	public BufferedImage createBufferedImageFromView() throws CreateException {
		return displayStackCurrentlyShown;
	}
	
	public BoundingBox createBoxForShiftedView( Point2i shift, Extent canvasExtnt ) {
		ReadableTuple3i crnrMin = this.bboxViewport.getCornerMin();
		
		int xNew = crnrMin.getX() + shift.getX();
		int yNew = crnrMin.getY() + shift.getY();
		
		Point2i pnt = new Point2i(xNew,yNew);
		pnt = DisplayStackViewportUtilities.clipToImage(pnt, bboxViewport.extent(), getDimensionsEntire() );
		pnt = DisplayStackViewportUtilities.clipToImage(pnt, canvasExtnt, getDimensionsEntire());
		assert(pnt.getX() >= 0);
		assert(pnt.getY() >= 0);
		// We need to clip
		
		Point3i pnt3 = new Point3i(pnt.getX(),pnt.getY(),this.bboxViewport.getCornerMin().getZ());
		
		assert( pnt3.getX() >= 0 );
		assert( pnt3.getY() >= 0 );
		
		return new BoundingBox(pnt3, bboxViewport.extent());
	}
	
	// Either updates the view and creates a new BufferedImage, or returns null if nothing changes
	public BufferedImage updateView( BoundingBox bbox, ZoomScale zoomScale ) throws OperationFailedException {
		assert(regionExtracter!=null);
			
		this.bboxViewport = bbox;
		this.zoomScale = zoomScale;
		assert( displayStackEntireImage.getDimensions().contains(bbox));
		
		try {
			return regionExtracter.extractRegionFrom( bbox, zoomScale.getScale() ).createBufferedImage();
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	// Using global coord
	public BufferedImage createPartOfCurrentView( BoundingBox bboxUpdate ) throws OperationFailedException {
		assert(regionExtracter!=null);
		assert( bboxViewport.contains().box(bboxUpdate) );
		
		try {
			DisplayStack ds = regionExtracter.extractRegionFrom( bboxUpdate, zoomScale.getScale() );
			return ds.createBufferedImage();
									
		} catch (CreateException e) {
			throw new OperationFailedException(e);
		}
	}
	
	
	public Point2i calcNewCrnrPosAfterChangeInViewSize( Extent extentOld, Extent extentNew, Point2i scrollValImage )
	{
		Extent diff = extentOld
				.subtract(extentNew.asTuple())
				.divide(2);

		addCond( scrollValImage, diff, extentOld );

		return DisplayStackViewportUtilities.clipToImage(scrollValImage, extentNew, getDimensionsEntire() );
	}

		
	private static void addCond( Point2i scrollVal, Extent toAdd, Extent cond ) {
		if (cond.getX()>0 && toAdd.getX()!=0) {
			scrollVal.setX( scrollVal.getX() + toAdd.getX() );
		}
		
		if (cond.getY()>0 && toAdd.getY()!=0) {
			scrollVal.setY( scrollVal.getY() + toAdd.getY() );					
		}		
	}
	
	// If the image point x,y is contained within the canvas
	public boolean canvasContainsAbs( int x, int y, int z ) {
		return displayStackEntireImage.getDimensions().contains( new Point3i(x,y,z) );
	}
	
	// Returns a string describing the intensity values at a particular absolute point in the display stack
	public String intensityStrAtAbs( int x, int y, int z ) {
		
		StringBuilder sb = new StringBuilder();
		
		int numChnl = displayStackEntireImage.getNumChnl();
		for( int c=0; c<numChnl; c++) {

			int intensVal = displayStackEntireImage.getUnconvertedVoxelAt(c, x, y, z);
			sb.append( String.format("%6d",intensVal) );
				
			if (c!=(numChnl-1)) {
				sb.append(",");
			}
		}
		
		return sb.toString();
	}
	
	// empty() means it cannot be determined
	public Optional<VoxelDataType> associatedDataType() {
		return displayStackEntireImage.unconvertedDataType();
	}
	
	public BoundOverlayedDisplayStack getDisplayStackEntireImage() {
		return displayStackEntireImage;
	}

	public ImageDimensions dim() {
		return displayStackEntireImage.getDimensions();
	}
	

}
