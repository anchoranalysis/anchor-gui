/* (C)2020 */
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
    public BufferedImage updateView(BoundingBox bbox) throws OperationFailedException {
        BoundingBox bboxScaled = bbox.scale(new ScaleFactor(zoomScale.getScaleInv()));

        // Scaling seemingly can produce a bbox that is slightly too-big
        bboxScaled =
                bboxScaled.clipTo(
                        delegate.getDisplayStackEntireImage().getDimensions().getExtent());
        assert (delegate.getDisplayStackEntireImage().getDimensions().contains(bboxScaled));
        return delegate.updateView(bboxScaled, zoomScale);
    }

    public BoundingBox createBoxForShiftedView(Point2i shift, Extent canvasExtent) {

        Point2i shiftImg = zoomScale.removeScale(shift);
        Extent canvasExtentImg = zoomScale.removeScale(canvasExtent);

        BoundingBox shiftedBox =
                delegate.createBoxForShiftedView(shiftImg, canvasExtentImg)
                        .scale(new ScaleFactor(zoomScale.getScale()));

        assert (shiftedBox.cornerMin().getX() >= 0);
        assert (shiftedBox.cornerMin().getY() >= 0);

        return shiftedBox;
    }

    private int cnvrtCanvasXToImage(int val, ZoomScale zs) {
        return zs.removeScale(val) + delegate.getBBox().cornerMin().getX();
    }

    private int cnvrtCanvasYToImage(int val, ZoomScale zs) {
        return zs.removeScale(val) + delegate.getBBox().cornerMin().getY();
    }

    public int cnvrtImageXToCanvas(int val) {
        return zoomScale.applyScale(val - delegate.getBBox().cornerMin().getX());
    }

    public int cnvrtImageYToCanvas(int val) {
        return zoomScale.applyScale(val - delegate.getBBox().cornerMin().getY());
    }

    public int cnvrtCanvasXToImage(int val) {
        return zoomScale.removeScale(val) + delegate.getBBox().cornerMin().getX();
    }

    public int cnvrtCanvasYToImage(int val) {
        return zoomScale.removeScale(val) + delegate.getBBox().cornerMin().getY();
    }

    public void setDisplayStackEntireImage(BoundOverlayedDisplayStack displayStack)
            throws SetOperationFailedException {
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
        return getDimensionsEntire().scaleXYBy(new ScaleFactor(zoomScale.getScale()));
    }

    public ImageResolution getRes() {
        return delegate.getDimensionsEntire().getRes();
    }

    public Point2i calcNewCrnrPosToMaintainMousePoint(Point2i mousePoint, ZoomScale zoomScaleOld) {

        // Mouse point is already in image-cordinates
        Point2i imgPointOld =
                new Point2i(
                        cnvrtCanvasXToImage(mousePoint.getX(), zoomScaleOld),
                        cnvrtCanvasYToImage(mousePoint.getY(), zoomScaleOld));

        // We want the mousePoint at the new scale, to be on the same img point
        Point2i imgPointNewGlobal = zoomScale.applyScale(imgPointOld);

        // Corner point
        Point2i crnrPoint = new Point2i();
        crnrPoint.setX(imgPointNewGlobal.getX() - mousePoint.getX());
        crnrPoint.setY(imgPointNewGlobal.getY() - mousePoint.getY());

        // But if they are less than 0, then we need to adjust
        crnrPoint.setX(Math.max(crnrPoint.getX(), 0));
        crnrPoint.setY(Math.max(crnrPoint.getY(), 0));
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
        return zoomScale.applyScale(delegate.getBBox());
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
