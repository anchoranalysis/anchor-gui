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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.scale.ScaleFactor;

class DisplayStackViewportZoomed {

    @Getter private DisplayStackViewport unzoomed = new DisplayStackViewport();
    @Getter @Setter private ZoomScale zoomScale = new ZoomScale();

    // Updates the view to bind to a new location, and returns a BufferedImage if has changed
    //   or null otherwise
    // The bounding box should refer to the Scaled space
    public BufferedImage updateView(BoundingBox box) throws OperationFailedException {
        BoundingBox boxScaled = box.scale(new ScaleFactor(zoomScale.getScaleInv()));

        // Scaling seemingly can produce a box that is slightly too-big
        boxScaled = boxScaled.clipTo(unzoomed.getDisplayStackEntireImage().dimensions().extent());
        assert (unzoomed.getDisplayStackEntireImage().dimensions().contains(boxScaled));
        return unzoomed.updateView(boxScaled, zoomScale);
    }

    public BoundingBox createBoxForShiftedView(Point2i shift, Extent canvasExtent) {

        Point2i shiftImg = zoomScale.removeScale(shift);
        Extent canvasExtentImg = zoomScale.removeScale(canvasExtent);

        BoundingBox shiftedBox =
                unzoomed.createBoxForShiftedView(shiftImg, canvasExtentImg)
                        .scale(new ScaleFactor(zoomScale.getScale()));

        assert (shiftedBox.cornerMin().x() >= 0);
        assert (shiftedBox.cornerMin().y() >= 0);

        return shiftedBox;
    }

    public int cnvrtImageXToCanvas(int val) {
        return zoomScale.applyScale(val - unzoomed.boundingBox().cornerMin().x());
    }

    public int cnvrtImageYToCanvas(int val) {
        return zoomScale.applyScale(val - unzoomed.boundingBox().cornerMin().y());
    }

    public int cnvrtCanvasXToImage(int val) {
        return zoomScale.removeScale(val) + unzoomed.boundingBox().cornerMin().x();
    }

    public int cnvrtCanvasYToImage(int val) {
        return zoomScale.removeScale(val) + unzoomed.boundingBox().cornerMin().y();
    }

    public void setDisplayStackEntireImage(BoundOverlayedDisplayStack displayStack) {
        unzoomed.setDisplayStackEntireImage(displayStack);
    }

    public Dimensions dimensionsEntire() {
        return unzoomed.dimensionsEntire();
    }

    public Dimensions createDimensionsEntireScaled() {
        return dimensionsEntire().scaleXYBy(new ScaleFactor(zoomScale.getScale()));
    }

    public Optional<Resolution> getResolution() {
        return unzoomed.dimensionsEntire().resolution();
    }

    public Point2i cornerToMaintainMousePoint(Point2i mousePoint, ZoomScale zoomScaleOld) {

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

    public Point2i cornerAfterChangeInZoom(
            Extent canvasExtentOld,
            ZoomScale zoomScaleOld,
            Extent canvasExtentNew,
            Point2i scrollValImage) {

        Extent canvasExtentNewImage = zoomScale.removeScale(canvasExtentNew);
        Extent canvasExtentOldImage = zoomScaleOld.removeScale(canvasExtentOld);

        Point2i scaleFree =
                unzoomed.cornerAfterChangeInViewSize(
                        canvasExtentOldImage, canvasExtentNewImage, scrollValImage);
        return zoomScale.applyScale(scaleFree);
    }

    public Point2i removeScale(Point2i point) {
        return zoomScale.removeScale(point);
    }

    public BoundingBox getBBox() {
        return zoomScale.applyScale(unzoomed.boundingBox());
    }

    // If the image point x,y is contained within the canvas
    public boolean canvasContainsAbs(Point3i point) {
        return unzoomed.canvasContainsAbs(point);
    }

    public String intensityStrAtAbs(Point3i point) {
        return unzoomed.intensityStrAtAbs(point);
    }

    // empty() means it cannot be determined
    public Optional<VoxelDataType> associatedDataType() {
        return unzoomed.associatedDataType();
    }

    public BoundOverlayedDisplayStack getDisplayStackEntireImage() {
        return unzoomed.getDisplayStackEntireImage();
    }

    private int cnvrtCanvasXToImage(int val, ZoomScale zs) {
        return zs.removeScale(val) + unzoomed.boundingBox().cornerMin().x();
    }

    private int cnvrtCanvasYToImage(int val, ZoomScale zs) {
        return zs.removeScale(val) + unzoomed.boundingBox().cornerMin().y();
    }
}
