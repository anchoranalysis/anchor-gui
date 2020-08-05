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

package org.anchoranalysis.gui.frame.canvas;

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
    public BufferedImage updateView(BoundingBox box) throws OperationFailedException {
        BoundingBox boxScaled = box.scale(new ScaleFactor(zoomScale.getScaleInv()));

        // Scaling seemingly can produce a box that is slightly too-big
        boxScaled =
                boxScaled.clipTo(
                        delegate.getDisplayStackEntireImage().dimensions().extent());
        assert (delegate.getDisplayStackEntireImage().dimensions().contains(boxScaled));
        return delegate.updateView(boxScaled, zoomScale);
    }

    public BoundingBox createBoxForShiftedView(Point2i shift, Extent canvasExtent) {

        Point2i shiftImg = zoomScale.removeScale(shift);
        Extent canvasExtentImg = zoomScale.removeScale(canvasExtent);

        BoundingBox shiftedBox =
                delegate.createBoxForShiftedView(shiftImg, canvasExtentImg)
                        .scale(new ScaleFactor(zoomScale.getScale()));

        assert (shiftedBox.cornerMin().x() >= 0);
        assert (shiftedBox.cornerMin().y() >= 0);

        return shiftedBox;
    }

    private int cnvrtCanvasXToImage(int val, ZoomScale zs) {
        return zs.removeScale(val) + delegate.boundingBox().cornerMin().x();
    }

    private int cnvrtCanvasYToImage(int val, ZoomScale zs) {
        return zs.removeScale(val) + delegate.boundingBox().cornerMin().y();
    }

    public int cnvrtImageXToCanvas(int val) {
        return zoomScale.applyScale(val - delegate.boundingBox().cornerMin().x());
    }

    public int cnvrtImageYToCanvas(int val) {
        return zoomScale.applyScale(val - delegate.boundingBox().cornerMin().y());
    }

    public int cnvrtCanvasXToImage(int val) {
        return zoomScale.removeScale(val) + delegate.boundingBox().cornerMin().x();
    }

    public int cnvrtCanvasYToImage(int val) {
        return zoomScale.removeScale(val) + delegate.boundingBox().cornerMin().y();
    }

    public void setDisplayStackEntireImage(BoundOverlayedDisplayStack displayStack)
            throws SetOperationFailedException {
        delegate.setDisplayStackEntireImage(displayStack);
    }

    public ImageDimensions dimensionsEntire() {
        return delegate.dimensionsEntire();
    }

    public ZoomScale getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(ZoomScale zoomScale) {
        this.zoomScale = zoomScale;
    }

    public ImageDimensions createDimensionsEntireScaled() {
        return dimensionsEntire().scaleXYBy(new ScaleFactor(zoomScale.getScale()));
    }

    public ImageResolution getRes() {
        return delegate.dimensionsEntire().resolution();
    }

    public Point2i calcNewCrnrPosToMaintainMousePoint(Point2i mousePoint, ZoomScale zoomScaleOld) {

        // Mouse point is already in image-cordinates
        Point2i imgPointOld =
                new Point2i(
                        cnvrtCanvasXToImage(mousePoint.x(), zoomScaleOld),
                        cnvrtCanvasYToImage(mousePoint.y(), zoomScaleOld));

        // We want the mousePoint at the new scale, to be on the same img point
        Point2i imgPointNewGlobal = zoomScale.applyScale(imgPointOld);

        // Corner point
        Point2i crnrPoint = new Point2i();
        crnrPoint.setX(imgPointNewGlobal.x() - mousePoint.x());
        crnrPoint.setY(imgPointNewGlobal.y() - mousePoint.y());

        // But if they are less than 0, then we need to adjust
        crnrPoint.setX(Math.max(crnrPoint.x(), 0));
        crnrPoint.setY(Math.max(crnrPoint.y(), 0));
        return crnrPoint;
    }

    public Point2i calcNewCrnrPosAfterChangeInZoom(
            Extent canvasExtentOld,
            ZoomScale zoomScaleOld,
            Extent canvasExtentNew,
            Point2i scrollValImage) {

        Extent canvasExtentNewImage = zoomScale.removeScale(canvasExtentNew);
        Extent canvasExtentOldImage = zoomScaleOld.removeScale(canvasExtentOld);

        Point2i scaleFree =
                delegate.calcNewCrnrPosAfterChangeInViewSize(
                        canvasExtentOldImage, canvasExtentNewImage, scrollValImage);
        return zoomScale.applyScale(scaleFree);
    }

    public Point2i removeScale(Point2i point) {
        return zoomScale.removeScale(point);
    }

    public BoundingBox getBBox() {
        return zoomScale.applyScale(delegate.boundingBox());
    }

    // If the image point x,y is contained within the canvas
    public boolean canvasContainsAbs(int x, int y, int z) {
        return delegate.canvasContainsAbs(x, y, z);
    }

    public String intensityStrAtAbs(int x, int y, int z) {
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
