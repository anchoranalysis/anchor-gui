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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.Optional;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.displayupdate.ProvidesDisplayUpdate;
import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.extent.Extent;
import org.anchoranalysis.spatial.extent.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point2i;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;

public class ImageCanvas {

    private static final Dimension MINIMUM_SIZE = new Dimension(200, 200);

    private ImageCanvasSwing canvas;

    private ProvidesDisplayUpdate imageProvider;

    private int slice = 0;

    private ErrorReporter errorReporter;

    // How we track what's currently displayed
    private DisplayStackViewportZoomed displayStackViewport;

    private EventListenerList eventList = new EventListenerList();

    private ExtentScrollBars extentScrollbars;

    private JPanel panel;

    private DefaultZoomSuggestor defaultZoomSuggestor = new DefaultZoomSuggestor(800, 600);

    private boolean enforceMinimumSizeAfterGuessZoom = false;

    public ImageCanvas() {
        this.panel = new JPanel();
        this.panel.setLayout(new BorderLayout());
        this.panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.panel.setMinimumSize(MINIMUM_SIZE);
    }

    private class UpdateImageListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {

            try {
                // We look for any changes that might have happened since last time
                applyPendingAndUpdate(displayStackViewport.getZoomScale());

            } catch (OperationFailedException e1) {
                errorReporter.recordError(ImageCanvas.class, e1);
            }
        }
    }

    private class UpdateViewportOnlyListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            try {
                updateViewportOnly();
            } catch (OperationFailedException e1) {
                errorReporter.recordError(ImageCanvas.class, e1);
            }
        }
    }

    public void init(ProvidesDisplayUpdate imageProvider, final ErrorReporter errorReporter)
            throws InitException {

        this.displayStackViewport = new DisplayStackViewportZoomed();

        this.errorReporter = errorReporter;
        this.imageProvider = imageProvider;
        this.imageProvider.addChangeListener(new UpdateImageListener());

        this.canvas = new ImageCanvasSwing();

        this.canvas.addMouseListener(
                new MouseListenerRelative(mouseEventsBroadCaster(MouseListener.class)));
        this.canvas.addMouseMotionListener(
                new MouseMotionListenerRelative(mouseEventsBroadCaster(MouseMotionListener.class)));

        this.canvas.addMouseWheelListener(new MouseWheelListenerZoom(this));

        MouseMotionListenerDragView mmlDragView =
                new MouseMotionListenerDragView(this, displayStackViewport, errorReporter);
        this.canvas.addMouseListener(mmlDragView);
        this.canvas.addMouseMotionListener(mmlDragView);

        this.extentScrollbars = new ExtentScrollBars();
        this.extentScrollbars.addChangeListener(new UpdateViewportOnlyListener());

        this.panel.add(canvas, BorderLayout.CENTER);
        this.panel.add(extentScrollbars.getScrollVer(), BorderLayout.EAST);
        this.panel.add(extentScrollbars.getScrollHor(), BorderLayout.SOUTH);

        try {
            // We look for any changes that might have happened since last time
            // Selecting a zoom level of null, means it's automatically selected
            applyPendingAndUpdate(null);

            if (enforceMinimumSizeAfterGuessZoom) {
                Extent e = displayStackViewport.getBBox().extent();
                panel.setMinimumSize(new Dimension(e.x(), e.y()));
            }

        } catch (OperationFailedException e) {
            throw new InitException(e);
        }

        addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(ComponentEvent e) {
                        super.componentResized(e);
                        resizeEventFromFrame();
                    }
                });
    }

    public void resizeEventFromFrame() {
        try {
            setScrollBarExtent();

            // Keeps the current zoom level
            updateImage(displayStackViewport.getZoomScale());
        } catch (OperationFailedException e1) {
            errorReporter.recordError(ImageCanvas.class, e1);
        }
    }

    // if absCoord out listener returns the absolute coordinates
    public void addMouseMotionListener(MouseMotionListener l, boolean absCoord) {
        // We use our own listeners, which instead use coordinates which take account of the zoom
        // factor
        if (absCoord) {
            canvas.addMouseMotionListener(l);
        } else {
            eventList.add(MouseMotionListener.class, l);
        }
    }

    public void addMouseListener(MouseListener l, boolean absCoord) {
        // We use our own listeners, which instead use coordinates which take account of the zoom
        // factor
        if (absCoord) {
            canvas.addMouseListener(l);
        } else {
            eventList.add(MouseListener.class, l);
        }
    }

    // ZoomesIn (with an optional mouse-point if the zoom is centered around a new point)
    public void zoomIn(Point2i mousePoint) {
        ZoomScale zoomScaleNew = displayStackViewport.getZoomScale().zoomIn();
        try {
            if (zoomScaleNew != null) {
                updateViewportOnly(zoomScaleNew, mousePoint);
            }
        } catch (OperationFailedException e) {
            errorReporter.recordError(ImageCanvas.class, e);
        }
    }

    public void zoomOut(Point2i mousePoint) {
        ZoomScale zoomScaleNew = displayStackViewport.getZoomScale().zoomOut();
        try {
            if (zoomScaleNew != null) {
                updateViewportOnly(zoomScaleNew, mousePoint);
            }
        } catch (OperationFailedException e) {
            errorReporter.recordError(ImageCanvas.class, e);
        }
    }

    public void shiftViewport(Point2i shift) throws OperationFailedException {

        BoundingBox shiftedBox =
                displayStackViewport.createBoxForShiftedView(shift, canvas.createExtent());

        assert (displayStackViewport.createDimensionsEntireScaled().contains(shiftedBox));

        extentScrollbars.setValue(
                new Point2i(shiftedBox.cornerMin().x(), shiftedBox.cornerMin().y()));

        updateStackViewportForImageExtent(shiftedBox.extent());
    }

    public void changeSlice(int z) {
        this.slice = z;

        try {
            Extent extent =
                    extentToRetrieveScaled(displayStackViewport.createDimensionsEntireScaled());
            updateStackViewportForImageExtent(extent);

        } catch (OperationFailedException e) {
            errorReporter.recordError(ImageCanvas.class, e);
        }
    }

    // Updates the image with a given zoomScale (null means it guesses)
    private void updateImage(ZoomScale zoomScaleNew) throws OperationFailedException {

        // If we don't have a zoom scale we try to pick an intelligent default
        if (zoomScaleNew == null) {
            zoomScaleNew =
                    defaultZoomSuggestor.suggestDefaultZoomFor(
                            displayStackViewport.getDisplayStackEntireImage().dimensions());
        }

        zoomScaleNew.establishBounds(displayStackViewport.dimensionsEntire());
        updateViewportOnly(zoomScaleNew, null);
    }

    private void updateViewportOnly(ZoomScale zoomScaleNew, Point2i mousePoint)
            throws OperationFailedException {

        Point2i scrollVal = displayStackViewport.removeScale(extentScrollbars.value());
        ZoomScale zoomScaleOld = displayStackViewport.getZoomScale();

        displayStackViewport.setZoomScale(zoomScaleNew);

        {
            // We interpolate a region from the image
            Extent extentNew =
                    extentToRetrieveScaled(displayStackViewport.createDimensionsEntireScaled());

            // We update the scrollbars as well
            updateScollBarsWithNewExtent(
                    extentNew, zoomScaleOld, zoomScaleNew, scrollVal, mousePoint);

            updateStackViewportForImageExtent(extentNew);
        }
    }

    private void updateScollBarsWithNewExtent(
            Extent extentNew,
            ZoomScale zoomScaleOld,
            ZoomScale zoomScaleNew,
            Point2i scrollVal,
            Point2i mousePoint) {

        Extent canvasExtentOld = canvas.createExtent();

        assert (displayStackViewport
                .createDimensionsEntireScaled()
                .contains(new BoundingBox(extentNew)));

        Point2i scrollValNew;
        // Only if we have a mouse point, and we are retrieving the same sized extent as previously
        if (mousePoint != null
                && extentNew.calculateVolume() == canvasExtentOld.calculateVolume()
                && zoomScaleNew.equals(zoomScaleOld)) {
            scrollValNew =
                    displayStackViewport.cornerToMaintainMousePoint(mousePoint, zoomScaleOld);
        } else {
            scrollValNew =
                    displayStackViewport.cornerAfterChangeInZoom(
                            canvasExtentOld, zoomScaleOld, extentNew, scrollVal);
        }

        setScrollBarExtent();
        extentScrollbars.setValue(scrollValNew);

        assert (displayStackViewport
                .createDimensionsEntireScaled()
                .contains(new BoundingBox(extentNew)));
    }

    private Extent extentToRetrieveScaled(Dimensions dimensionsScaled) {
        // We calculate the size of the region based upon the current size of the canvas (if we can)

        if (canvas.getWidth() > 0) {
            int sx = canvas.getWidth();
            int sy = canvas.getHeight();
            sx = Math.min(sx, dimensionsScaled.x());
            sy = Math.min(sy, dimensionsScaled.y());
            return new Extent(sx, sy);
        } else {
            Dimensions dimensionsEntire = displayStackViewport.createDimensionsEntireScaled();
            int sx = Math.min(dimensionsEntire.x(), dimensionsScaled.x());
            int sy = Math.min(dimensionsEntire.y(), dimensionsScaled.y());
            return new Extent(sx, sy, 1);
        }
    }

    private void updateStackViewportForImageExtent(Extent extentImageSc)
            throws OperationFailedException {
        assert (displayStackViewport
                .createDimensionsEntireScaled()
                .contains(new BoundingBox(extentImageSc)));

        Point2i scrollVal = extentScrollbars.value();
        Point3i cornerMin = new Point3i(scrollVal.x(), scrollVal.y(), slice);
        BoundingBox boxView = new BoundingBox(cornerMin, extentImageSc);

        // We ignore nulls, as it means nothing has changed
        BufferedImage biUpdate = displayStackViewport.updateView(boxView);
        if (biUpdate != null) {
            canvas.updated(biUpdate);
        }

        updateSizeOnPanel();
    }

    private void updateSizeOnPanel() {
        Dimensions dimensions = displayStackViewport.createDimensionsEntireScaled();
        panel.setPreferredSize(
                new Dimension(
                        dimensions.x() + extentScrollbars.getPreferredWidth(),
                        dimensions.y() + extentScrollbars.getPreferredHeight()));
    }

    // Applies any pending changes, and then updates the image
    private void applyPendingAndUpdate(ZoomScale zoomLevel) throws OperationFailedException {

        DisplayUpdate ds = imageProvider.get();

        // If there's no update, just update the entire image
        if (ds == null) {
            System.out.println("Null so updating everything");
            updateImage(zoomLevel);
            return;
        }

        // If the diplayStack has changed, then update the entire image
        if (ds.getDisplayStack() != null) {

            // Then redraw parts should be null, as we have to change the displaystack anyway
            assert (ds.getRedrawParts() == null);

            if (slice > ds.getDisplayStack().dimensions().z()) {
                slice = ds.getDisplayStack().dimensions().z() - 1;
            }

            // Let's avoid unnecessary updates
            if (displayStackViewport.getDisplayStackEntireImage() != ds.getDisplayStack()) {
                displayStackViewport.setDisplayStackEntireImage(ds.getDisplayStack());
            }
            updateImage(zoomLevel);
            return;
        }

        // If no redraw parts, or no existing buffer, then update everything
        if (ds.getRedrawParts() == null || !canvas.hasBeenUpdated()) {
            updateImage(zoomLevel);
            return;
        }

        // If we get to here then we only update the region with redraw parts
        BoundingBox boxCurrentDisplayed = displayStackViewport.getUnzoomed().boundingBox();

        // If we get to here, we don't change the viewport, but simply repaint some
        //   of the canvas using a section of the viewport
        for (BoundingBox box : ds.getRedrawParts()) {

            // If it intersects with our current viewport
            if (box.intersection().existsWith(boxCurrentDisplayed)) {

                BoundingBox boxIntersect =
                        box.intersection()
                                .withInside(
                                        boxCurrentDisplayed,
                                        displayStackViewport
                                                .getUnzoomed()
                                                .dimensionsEntire()
                                                .extent())
                                .orElseThrow(
                                        () ->
                                                new OperationFailedException(
                                                        "The bounding-box does not intersect with the viewport"));

                BufferedImage bi =
                        displayStackViewport.getUnzoomed().createPartOfCurrentView(boxIntersect);

                // Impose the bi on top of the existing fi
                int xCanvas =
                        displayStackViewport.cnvrtImageXToCanvas(boxIntersect.cornerMin().x());
                int yCanvas =
                        displayStackViewport.cnvrtImageYToCanvas(boxIntersect.cornerMin().y());

                assert (xCanvas >= 0);
                assert (yCanvas >= 0);

                canvas.updatePart(bi, xCanvas, yCanvas);
            }
        }
    }

    private void updateViewportOnly() throws OperationFailedException {
        updateViewportOnly(displayStackViewport.getZoomScale(), null);
    }

    public void addMouseWheelListener(MouseWheelListener l) {
        canvas.addMouseWheelListener(l);
    }

    public boolean isEnforceMinimumSizeAfterGuessZoom() {
        return enforceMinimumSizeAfterGuessZoom;
    }

    public void setEnforceMinimumSizeAfterGuessZoom(boolean enforceMinimumSizeAfterGuessZoom) {
        this.enforceMinimumSizeAfterGuessZoom = enforceMinimumSizeAfterGuessZoom;
    }

    public void addComponentListener(ComponentListener l) {
        canvas.addComponentListener(l);
    }

    public void setDefaultZoomSuggestor(DefaultZoomSuggestor defaultZoomSuggestor) {
        this.defaultZoomSuggestor = defaultZoomSuggestor;
    }

    public void setScrollBarExtent() {

        Extent canvasExtent = canvas.createExtent();

        extentScrollbars.setVisibleAmount(canvasExtent);

        // If the viewport is much smaller than our canvas size, then we allow the scrollbars to
        // disappear
        // If we do this, when the size is similar, we can end up in endless loops of the scrollbars
        // appearing
        //   disappearing, causing flickering, so we only do with the size is less than the approz
        // size of the scrollbar

        Extent entireImageExtent = displayStackViewport.createDimensionsEntireScaled().extent();

        boolean alwaysAllowChangeInVisibility =
                minSizeDifferenceXY(canvasExtent, entireImageExtent)
                        > (extentScrollbars.maxSizeOfScrollbar() + 1);
        extentScrollbars.setMinMax(entireImageExtent, alwaysAllowChangeInVisibility);
    }

    public int getSlice() {
        return slice;
    }

    /** If the image point x,y is contained within the canvas? */
    public boolean canvasContainsAbsolute(Point2i point) {
        return displayStackViewport.canvasContainsAbs(PointConverter.convertTo3i(point, slice));
    }

    public String intensityStrAtAbsolute(Point2i point) {
        return displayStackViewport.intensityStrAtAbs(PointConverter.convertTo3i(point, slice));
    }

    // Null means it cannot be determined
    public Optional<VoxelDataType> associatedDataType() {
        return displayStackViewport.associatedDataType();
    }

    public void dispose() {

        {
            ComponentListener[] listeners = canvas.getComponentListeners();
            for (ComponentListener item : listeners) {
                canvas.removeComponentListener(item);
            }
        }
        {
            MouseWheelListener[] listeners = canvas.getMouseWheelListeners();
            for (MouseWheelListener item : listeners) {
                canvas.removeMouseWheelListener(item);
            }
        }
        {
            MouseMotionListener[] listeners = canvas.getMouseMotionListeners();
            for (MouseMotionListener item : listeners) {
                canvas.removeMouseMotionListener(item);
            }
        }
        {
            MouseListener[] listeners = canvas.getMouseListeners();
            for (MouseListener item : listeners) {
                canvas.removeMouseListener(item);
            }
        }

        displayStackViewport = null;
        imageProvider = null;
        canvas = null;
        panel.removeAll();
        panel = null;
        eventList = null;
    }

    public boolean requestFocusInWindow() {
        return canvas.requestFocusInWindow();
    }

    // START Getters and Setters

    public Dimensions dimensions() {
        return displayStackViewport.dimensionsEntire();
    }

    public Component getPanel() {
        return panel;
    }

    public Dimension getMinimumSize() {
        return panel.getMinimumSize();
    }

    public Dimension getPrefferedSize() {
        return panel.getPreferredSize();
    }

    public Optional<Resolution> getResolution() {
        return displayStackViewport.getResolution();
    }

    public ZoomScale getZoomScale() {
        return displayStackViewport.getZoomScale();
    }

    public final InputMap getInputMap() {
        return canvas.getInputMap();
    }

    public final ActionMap getActionMap() {
        return canvas.getActionMap();
    }

    private static int minSizeDifferenceXY(Extent first, Extent second) {
        int diffX = first.x() - second.x();
        int diffY = first.y() - second.y();
        return Math.min(diffX, diffY);
    }

    private <T extends EventListener> BroadcastMouseEvents<T> mouseEventsBroadCaster(
            Class<T> listenerType) {
        return new BroadcastMouseEvents<>(
                new MouseEventCreator(canvas, displayStackViewport), eventList, listenerType);
    }
}
