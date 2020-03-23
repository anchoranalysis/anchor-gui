package org.anchoranalysis.gui.frame.canvas.zoom;

import org.anchoranalysis.anchor.mpp.bounds.RslvdBound;

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
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ZoomScale {

	private int exp = 0;
	private double scale = 1;
	private double scaleInv = 1;
	
	
	// A bound on the exponent
	private RslvdBound boundZoom;
	
	private static int minWidth = 10;
	private static int minHeight = 10;
	private static int largestZoomExp = 5;
	
	public ZoomScale() {
		exp = 0;
		updateScale();
	}
	
	public ZoomScale( int exp ) {
		this.exp = exp;
		updateScale();
	}
	
	@Override
	public String toString() {
		return String.format("exp=%d",exp);
	}
	
	private void updateScale() {
		scale = Math.pow(2,exp);
		scaleInv = 1/scale;
	}
	
	public ZoomScale( int exp, RslvdBound boundZoom ) {
		this.exp = exp;
		this.boundZoom = boundZoom;
		updateScale();
	}
	
	
	private static int smallestZoom( int minExtnt, int actualExtnt ) {
		double ratio = ((double) minExtnt) / actualExtnt;
		double log = Math.log(ratio)/Math.log(2.0);
		return (int) Math.ceil(log);
	}
	
	// Establishes an upper and lower limit for zooming
	public void establishBounds( ImageDim dim ) {
		
		
		// The smallest zoom that keeps out size above 10, or 1
		// ceil( x = log2( minWidth / actualWidth ) )
		int smallestZoomX = smallestZoom(minWidth, dim.getX());
		int smallestZoomY = smallestZoom(minHeight, dim.getY());
		int smallestZoomExp = Math.max(smallestZoomX, smallestZoomY);
		
		// The largest zoom is 10
		boundZoom = new RslvdBound( smallestZoomExp, largestZoomExp );
	}
	
	public double getScale() {
		return scale;
	}
	
	public double getScaleInv() {
		return scaleInv;
	}
	
	public ZoomScale zoomIn() {
		int expNew = exp + 1;
		if (boundZoom.contains(expNew)) {
			return new ZoomScale( expNew, boundZoom );
		} else {
			return null;
		}
	}
	
	public ZoomScale zoomOut() {
		int expNew = exp - 1;
		if (boundZoom.contains(expNew)) {
			return new ZoomScale( expNew, boundZoom );
		} else {
			return null;
		}
	}
		
	public int applyScale( int val ) {
		return (int) (val*scale);
	}
		
	public Point2i applyScale( Point2i pnt ) {
		Point2i out = new Point2i();
		out.setX( applyScale( pnt.getX() ) );
		out.setY( applyScale( pnt.getY() ) );
		return out;
	}
	
	public BoundingBox applyScale( BoundingBox bbox ) {
		BoundingBox out = new BoundingBox(bbox);
		out.scaleXYPosAndExtnt( new ScaleFactor(scale) );
		return out;
	}
		
	public Extent applyScale( Extent e ) {
		Extent out = new Extent();
		out.setX( applyScale( e.getX() ) );
		out.setY( applyScale( e.getY() ) );
		out.setZ( e.getZ() );
		return out;
	}
	
	
	public Point2i removeScale( Point2i pnt ) {
		Point2i out = new Point2i();
		out.setX( removeScale( pnt.getX() ) );
		out.setY( removeScale( pnt.getY() ) );
		return out;
	}
	
	public Extent removeScale( Extent e ) {
		Extent out = new Extent();
		out.setX( removeScale( e.getX() ) );
		out.setY( removeScale( e.getY() ) );
		out.setZ( e.getZ() );
		return out;
	}
	
	// Resolves a value expressed in scaled co-ordinates into image-ocordinates
	public int removeScale( int val ) {
		return (int) (val/scale);
	}
	
	public int asPercentage() {
		return (int) (scale * 100);
	}
	
	@Override
	public boolean equals( Object obj ) {
		if (obj==this) {
			return true;
		}
		if (!(obj instanceof ZoomScale)) {
			return false;
		}
		ZoomScale objCast = (ZoomScale) obj;
		
		if (objCast.exp!=exp) {
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(exp).hashCode();
	}
	
	
}
