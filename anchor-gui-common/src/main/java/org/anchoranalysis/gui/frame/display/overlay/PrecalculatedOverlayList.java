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

package org.anchoranalysis.gui.frame.display.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.voxel.binary.values.BinaryValues;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;
import org.anchoranalysis.spatial.box.BoundingBox;

class PrecalculatedOverlayList {

    /** A colored configuration, the main data item. No nulls. */
    private ColoredOverlayCollection overlayCollection;

    /** Obj-masks generated from overlayCollection. No nulls. */
    private List<PrecalculationOverlay> generatedObjects;

    /** Bounding-boxes derived from overlayCollection. No nulls. */
    private List<BoundingBox> listBoundingBox;

    /**
     * obj-masks at different zoomlevel. Can contain nulls (meaning not yet calculated). Should
     * either be null (not-existing). Or be the same size as generatedObjects;
     */
    private List<PrecalculationOverlay> generatedObjectsZoomed;

    public PrecalculatedOverlayList() {
        overlayCollection = new ColoredOverlayCollection();
        generatedObjects = new ArrayList<>();
        listBoundingBox = new ArrayList<>();
        generatedObjectsZoomed = new ArrayList<>();
    }

    public PrecalculatedOverlayList(
            ColoredOverlayCollection overlayCollection,
            Dimensions dimEntireImage,
            DrawOverlay drawOverlay)
            throws CreateException {
        this.overlayCollection = overlayCollection;
        rebuild(dimEntireImage, drawOverlay);
    }

    public void assertListsSizeMatch() {
        assertSizesMatchSimple();
        assert (overlayCollection.size() == generatedObjects.size());
        assert (listBoundingBox.size() == generatedObjects.size());
        assert (generatedObjectsZoomed == null
                || generatedObjectsZoomed.size() == generatedObjects.size());
    }

    public void assertSizesMatchSimple() {
        assert (listBoundingBox.size() == overlayCollection.size());
    }

    public void assertZoomedExists() {
        assert (generatedObjectsZoomed != null);
    }

    public void setOverlayCollection(ColoredOverlayCollection overlayCollection) {
        this.overlayCollection = overlayCollection;
    }

    public void rebuild(Dimensions dimEntireImage, DrawOverlay drawOverlay) throws CreateException {
        generatedObjects =
                DrawOverlay.precalculate(
                        overlayCollection,
                        drawOverlay,
                        dimEntireImage,
                        BinaryValues.getDefault().createByte());
        listBoundingBox = overlayCollection.boxList(drawOverlay, dimEntireImage);
        generatedObjectsZoomed = null;
    }

    public void add(
            Overlay overlay,
            RGBColor color,
            PrecalculationOverlay precalculation,
            BoundingBox box,
            Optional<PrecalculationOverlay> precalculationZoomed) {
        overlayCollection.add(overlay, color);
        generatedObjects.add(precalculation);
        listBoundingBox.add(box);
        precalculationZoomed.ifPresent(generatedObjectsZoomed::add);
    }

    public void remove(int index) {
        overlayCollection.remove(index);
        generatedObjects.remove(index);
        listBoundingBox.remove(index);

        if (generatedObjectsZoomed != null) {
            generatedObjectsZoomed.remove(index);
        }
    }

    public void setZoomedToNull() {
        generatedObjectsZoomed = createCollectionWithNulls(size());
    }

    public Overlay getOverlay(int index) {
        return overlayCollection.get(index);
    }

    public RGBColor getColor(int index) {
        return overlayCollection.getColor(index);
    }

    public PrecalculationOverlay getPrecalculation(int index) {
        return generatedObjects.get(index);
    }

    public PrecalculationOverlay getPrecalculationZoomed(int index) {
        return generatedObjectsZoomed.get(index);
    }

    public List<PrecalculationOverlay> getListGeneratedObjects() {
        return generatedObjects;
    }

    public List<PrecalculationOverlay> getListGeneratedObjectsZoomed() {
        return generatedObjectsZoomed;
    }

    public List<BoundingBox> getListBoundingBox() {
        return listBoundingBox;
    }

    public boolean hasGeneratedObjectsZoomed() {
        return generatedObjectsZoomed != null;
    }

    public ColoredOverlayCollection getOverlayCollection() {
        return overlayCollection;
    }

    public BoundingBox getBBox(int index) {
        return listBoundingBox.get(index);
    }

    public int size() {
        return listBoundingBox.size();
    }

    public PrecalculationOverlay setPrecalculationZoomed(int index, PrecalculationOverlay element) {
        return generatedObjectsZoomed.set(index, element);
    }

    private static List<PrecalculationOverlay> createCollectionWithNulls(int size) {
        List<PrecalculationOverlay> out = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            out.add(null);
        }
        return out;
    }
}
