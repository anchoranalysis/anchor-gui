/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.frame.display;

import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.overlay.OverlayPrecalculatedCache;
import org.anchoranalysis.image.dimensions.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.overlay.writer.ObjectDrawAttributesFactory;

public class BoundColoredOverlayCollection {

    private DrawOverlay drawOverlay;

    private IDGetter<Overlay> idGetter;

    private Dimensions dimEntireImage;

    // The current overlay, with additional cached objects
    private OverlayPrecalculatedCache cache;

    public BoundColoredOverlayCollection(
            DrawOverlay drawOverlay, IDGetter<Overlay> idGetter, Dimensions dim)
            throws CreateException {
        super();
        this.drawOverlay = drawOverlay;
        this.idGetter = idGetter;
        this.dimEntireImage = dim;
        this.cache =
                new OverlayPrecalculatedCache(
                        new ColoredOverlayCollection(), dimEntireImage, drawOverlay);
    }

    public void updateDrawer(DrawOverlay drawOverlay) throws SetOperationFailedException {
        this.drawOverlay = drawOverlay;
        this.cache.setDrawer(drawOverlay);
    }

    public void addOverlays(ColoredOverlayCollection oc) throws OperationFailedException {
        this.cache.addOverlays(oc);
    }

    // Note that the order of items in indices is changed during processing
    public void removeOverlays(List<Integer> indices) {
        this.cache.removeOverlays(indices);
    }

    public void setOverlayCollection(ColoredOverlayCollection oc)
            throws SetOperationFailedException {
        this.cache.setOverlayCollection(oc);
    }

    public void drawRGB(RGBStack stack, BoundingBox box, double zoomFactor)
            throws OperationFailedException {

        // Create a containing bounding box with the zoom
        BoundingBox container = createZoomedContainer(box, zoomFactor, stack.extent());

        OverlayPrecalculatedCache marksWithinView =
                cache.subsetWithinView(box, container, zoomFactor);

        drawOverlay.writePrecalculatedOverlays(
                marksWithinView.getGeneratedObjectsZoomed(),
                dimEntireImage,
                stack,
                ObjectDrawAttributesFactory.createFromOverlays(
                        marksWithinView.getOverlays(), idGetter, new IDGetterIter<>()),
                container);
    }

    private static BoundingBox createZoomedContainer(
            BoundingBox box, double zoomFactor, Extent stackExtent) {
        Point3i cornerMin = new Point3i(box.cornerMin());
        cornerMin.scaleXY(zoomFactor);
        return new BoundingBox(cornerMin, stackExtent);
    }

    // Note the overlay do not actually have to be contained in the OverlayCollection for this to
    // work
    //  it will work with any overlay.... simply using the settings from the bound drawOverlay
    public List<BoundingBox> boxList(ColoredOverlayCollection oc) {
        return oc.boxList(drawOverlay, dimEntireImage);
    }

    public List<BoundingBox> boxList(OverlayCollection oc) {
        return oc.boxList(drawOverlay, dimEntireImage);
    }

    public synchronized OverlayPrecalculatedCache getPrecalculatedCache() {
        return cache;
    }
}
