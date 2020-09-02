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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.region.RegionExtracter;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

// Shows a certain amount of a stack at any given time
class DisplayStackViewport {

    private BoundingBox boxViewport = null;
    private ZoomScale zoomScale;

    private BoundOverlayedDisplayStack displayStackEntireImage;

    private BufferedImage displayStackCurrentlyShown;

    /** How we extract bounding-boxes of pixels */
    private RegionExtracter regionExtracter;

    public void setDisplayStackEntireImage(BoundOverlayedDisplayStack displayStack)
            throws SetOperationFailedException {
        this.displayStackEntireImage = displayStack;

        // We get a new regionExtracter as the displaystack has changed
        regionExtracter = displayStack.createRegionExtracter();
    }

    public Dimensions dimensionsEntire() {
        return displayStackEntireImage.dimensions();
    }

    public BoundingBox boundingBox() {
        return boxViewport;
    }

    public BufferedImage createBufferedImageFromView() throws CreateException {
        return displayStackCurrentlyShown;
    }

    public BoundingBox createBoxForShiftedView(Point2i shift, Extent canvasExtent) {
        ReadableTuple3i cornerMin = this.boxViewport.cornerMin();

        int xNew = cornerMin.x() + shift.x();
        int yNew = cornerMin.y() + shift.y();

        Point2i point = new Point2i(xNew, yNew);
        point =
                DisplayStackViewportUtilities.clipToImage(
                        point, boxViewport.extent(), dimensionsEntire());
        point = DisplayStackViewportUtilities.clipToImage(point, canvasExtent, dimensionsEntire());
        assert (point.x() >= 0);
        assert (point.y() >= 0);
        // We need to clip

        Point3i point3 = new Point3i(point.x(), point.y(), this.boxViewport.cornerMin().z());

        assert (point3.x() >= 0);
        assert (point3.y() >= 0);

        return new BoundingBox(point3, boxViewport.extent());
    }

    // Either updates the view and creates a new BufferedImage, or returns null if nothing changes
    public BufferedImage updateView(BoundingBox box, ZoomScale zoomScale)
            throws OperationFailedException {
        assert (regionExtracter != null);

        this.boxViewport = box;
        this.zoomScale = zoomScale;
        assert (displayStackEntireImage.dimensions().contains(box));

        try {
            return regionExtracter
                    .extractRegionFrom(box, zoomScale.getScale())
                    .createBufferedImage();
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // Using global coord
    public BufferedImage createPartOfCurrentView(BoundingBox boxUpdate)
            throws OperationFailedException {
        assert (regionExtracter != null);
        assert (boxViewport.contains().box(boxUpdate));

        try {
            DisplayStack ds = regionExtracter.extractRegionFrom(boxUpdate, zoomScale.getScale());
            return ds.createBufferedImage();

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    public Point2i cornerAfterChangeInViewSize(
            Extent extentOld, Extent extentNew, Point2i scrollValImage) {
        Extent diff = extentOld.subtract(extentNew.asTuple()).divide(2);

        addCond(scrollValImage, diff, extentOld);

        return DisplayStackViewportUtilities.clipToImage(
                scrollValImage, extentNew, dimensionsEntire());
    }

    private static void addCond(Point2i scrollVal, Extent toAdd, Extent cond) {
        if (cond.x() > 0 && toAdd.x() != 0) {
            scrollVal.setX(scrollVal.x() + toAdd.x());
        }

        if (cond.y() > 0 && toAdd.y() != 0) {
            scrollVal.setY(scrollVal.y() + toAdd.y());
        }
    }

    // If the image point x,y is contained within the canvas
    public boolean canvasContainsAbs(Point3i point) {
        return displayStackEntireImage.dimensions().contains(point);
    }

    // Returns a string describing the intensity values at a particular absolute point in the
    // display stack
    public String intensityStrAtAbs(Point3i point) {

        StringBuilder sb = new StringBuilder();

        int numberChannels = displayStackEntireImage.getNumberChannels();
        for (int c = 0; c < numberChannels; c++) {

            int intensity = displayStackEntireImage.getUnconvertedVoxelAt(c, point);
            sb.append(String.format("%6d", intensity));

            if (c != (numberChannels - 1)) {
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

    public Dimensions dim() {
        return displayStackEntireImage.dimensions();
    }
}
