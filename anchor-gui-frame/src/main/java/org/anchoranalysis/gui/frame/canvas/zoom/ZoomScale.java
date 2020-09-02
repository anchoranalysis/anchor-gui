/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.frame.canvas.zoom;

import lombok.Getter;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.mpp.bean.bound.ResolvedBound;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ZoomScale {

    private static final int MIN_WIDTH = 10;
    private static final int MIN_HEIGHT = 10;
    private static final int LARGEST_ZOOM_EXP = 5;

    private int exp = 0;

    @Getter private double scale = 1;

    @Getter private double scaleInv = 1;

    // A bound on the exponent
    private ResolvedBound boundZoom;

    public ZoomScale() {
        exp = 0;
        updateScale();
    }

    public ZoomScale(int exp) {
        this.exp = exp;
        updateScale();
    }

    @Override
    public String toString() {
        return String.format("exp=%d", exp);
    }

    private void updateScale() {
        scale = Math.pow(2, exp);
        scaleInv = 1 / scale;
    }

    public ZoomScale(int exp, ResolvedBound boundZoom) {
        this.exp = exp;
        this.boundZoom = boundZoom;
        updateScale();
    }

    private static int smallestZoom(int minExtent, int actualExtent) {
        double ratio = ((double) minExtent) / actualExtent;
        double log = Math.log(ratio) / Math.log(2.0);
        return (int) Math.ceil(log);
    }

    // Establishes an upper and lower limit for zooming
    public void establishBounds(Dimensions dimensions) {

        // The smallest zoom that keeps out size above 10, or 1
        // ceil( x = log2( minWidth / actualWidth ) )
        int smallestZoomX = smallestZoom(MIN_WIDTH, dimensions.x());
        int smallestZoomY = smallestZoom(MIN_HEIGHT, dimensions.y());
        int smallestZoomExp = Math.max(smallestZoomX, smallestZoomY);

        // The largest zoom is 10
        boundZoom = new ResolvedBound(smallestZoomExp, LARGEST_ZOOM_EXP);
    }

    public ZoomScale zoomIn() {
        int expNew = exp + 1;
        if (boundZoom.contains(expNew)) {
            return new ZoomScale(expNew, boundZoom);
        } else {
            return null;
        }
    }

    public ZoomScale zoomOut() {
        int expNew = exp - 1;
        if (boundZoom.contains(expNew)) {
            return new ZoomScale(expNew, boundZoom);
        } else {
            return null;
        }
    }

    public int applyScale(int val) {
        return (int) (val * scale);
    }

    public Point2i applyScale(Point2i point) {
        Point2i out = new Point2i();
        out.setX(applyScale(point.x()));
        out.setY(applyScale(point.y()));
        return out;
    }

    public BoundingBox applyScale(BoundingBox box) {
        return box.scale(new ScaleFactor(scale));
    }

    public Extent applyScale(Extent e) {
        return new Extent(applyScale(e.x()), applyScale(e.y()), e.z());
    }

    public Point2i removeScale(Point2i point) {
        return new Point2i(removeScale(point.x()), removeScale(point.y()));
    }

    public Extent removeScale(Extent e) {
        return new Extent(removeScale(e.x()), removeScale(e.y()), e.z());
    }

    // Resolves a value expressed in scaled coordinates into image-ocordinates
    public int removeScale(int val) {
        return (int) (val / scale);
    }

    public int asPercentage() {
        return (int) (scale * 100);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ZoomScale)) {
            return false;
        }
        ZoomScale objCast = (ZoomScale) obj;

        if (objCast.exp != exp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(exp).hashCode();
    }
}
