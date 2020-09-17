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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.extent.rtree.RTree;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;
import org.anchoranalysis.overlay.writer.DrawOverlay;
import org.anchoranalysis.overlay.writer.PrecalculationOverlay;

public class OverlayPrecalculatedCache implements OverlayRetriever {

    private PrecalculatedOverlayList overlayList;

    /**
     * Spatially indexes the bounding boxes in listBoundingBox (using the array index). Created when
     * needed.
     */
    private RTree<Integer> rTree = null;

    /**
     * The zoomFactor associated with the generatedObjectsZoomed cache. -1 indicates there is none
     * set. In this case, generatedObjectsZoomed should be null.
     */
    private double zoomFactor = -1;

    private DrawOverlay drawOverlay;

    private Dimensions dimEntireImage;

    private Dimensions dimScaled;

    /** The binary values we use for making object-masks */
    private static final BinaryValuesByte bvOut = BinaryValues.getDefault().createByte();

    public OverlayPrecalculatedCache(
            ColoredOverlayCollection overlayCollection,
            Dimensions dimEntireImage,
            DrawOverlay drawOverlay)
            throws CreateException {
        super();
        this.drawOverlay = drawOverlay;
        this.dimEntireImage = dimEntireImage;
        this.overlayList =
                new PrecalculatedOverlayList(overlayCollection, dimEntireImage, drawOverlay);
        rebuildCache();
    }

    public synchronized void setOverlayCollection(ColoredOverlayCollection overlayCollection)
            throws SetOperationFailedException {
        overlayList.setOverlayCollection(overlayCollection);
        try {
            rebuildCache();
        } catch (CreateException e) {
            throw new SetOperationFailedException(e);
        }
    }

    public synchronized void setDrawer(DrawOverlay drawOverlay) throws SetOperationFailedException {
        this.drawOverlay = drawOverlay;
        try {
            rebuildCache();
        } catch (CreateException e) {
            throw new SetOperationFailedException(e);
        }
    }

    /** Creates a subset which only contains marks contained within a particular bounding-box */
    public synchronized OverlayPrecalculatedCache subsetWithinView(
            BoundingBox boxView, BoundingBox boxViewZoomed, double zoomFactorNew)
            throws OperationFailedException {

        overlayList.assertSizesMatchSimple();

        // If we haven't bother initializing these things before, we do now
        if (rTree == null) {
            rTree = createRTreeOfIndices(overlayList.getListBoundingBox(), 10000);
        }
        if (overlayList.hasGeneratedObjectsZoomed() || zoomFactorNew != zoomFactor) {
            overlayList.setZoomedToNull();
        }
        overlayList.assertZoomedExists();

        // Create appropriate objects related to the zoom (if it's not 1)
        if (zoomFactorNew != 1 && zoomFactorNew != zoomFactor) {
            // We create a scaled version of our dimensions
            dimScaled = createDimensionsScaled(zoomFactorNew);
        }

        // Figure out which indices intersect with our bounding-box
        List<Integer> intersectingIndices = rTree.intersectsWith(boxView);

        // The lists for the subset we are creating
        PrecalculatedOverlayList precalculationOverlayList = new PrecalculatedOverlayList();

        // Loop through each index, and add to the output lists
        for (int i : intersectingIndices) {

            BoundingBox box = overlayList.getBBox(i);

            if (box.intersection().existsWith(boxView)) {

                overlayList.assertZoomedExists();

                addedScaledIndexTo(i, box, boxViewZoomed, zoomFactorNew, precalculationOverlayList);
            }
        }

        // We update the zoomFactor. Processing until now relies on the old zoomfactor
        this.zoomFactor = zoomFactorNew;

        overlayList.assertSizesMatchSimple();

        // We create our subset
        return new OverlayPrecalculatedCache(precalculationOverlayList, zoomFactor, dimEntireImage);
    }

    public synchronized void addOverlays(ColoredOverlayCollection overlaysToAdd)
            throws OperationFailedException {

        try {
            for (int i = 0; i < overlaysToAdd.size(); i++) {

                Overlay overlay = overlaysToAdd.get(i);

                ObjectWithProperties object =
                        overlay.createObject(drawOverlay, dimEntireImage, bvOut);
                PrecalculationOverlay precalculation =
                        DrawOverlay.createPrecalc(drawOverlay, object, dimEntireImage);

                BoundingBox box = overlay.box(drawOverlay, dimEntireImage);

                overlayList.add(
                        overlay, overlaysToAdd.getColor(i), precalculation, box, Optional.empty());

                if (rTree != null) {
                    // We add it under the ID of what we've just added
                    rTree.add(box, overlayList.size() - 1);
                }

                overlayList.assertSizesMatchSimple();
            }
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
        overlayList.assertSizesMatchSimple();
    }

    // Note that the order of items in indices is changed during processing
    public synchronized void removeOverlays(List<Integer> indices) {

        // Sort indices in descending order, so that we can remove without changing the order of
        // other items
        Collections.sort(indices, Collections.reverseOrder());

        for (int i : indices) {
            // Remove each item
            overlayList.remove(i);
        }

        // We invalidate the rTree as our indices now change completely.
        // TODO we can make this more efficient by updating indices in the rTree (using a pointer
        // object) rather than deleting it from scratch
        //  and recreating

        rTree = null;
        overlayList.assertSizesMatchSimple();
    }

    // Finds all the overlays at a given point
    @Override
    public OverlayCollection overlaysAt(Point3i point) {
        OverlayCollection out = new OverlayCollection();
        List<Integer> ids = rTree.contains(point);

        for (Integer id : ids) {

            Overlay overlay = overlayList.getOverlay(id);

            // Check if it's actually inside
            if (overlay.isPointInside(drawOverlay, point)) {
                out.add(overlay);
            }
        }
        return out;
    }

    @Override
    public synchronized ColoredOverlayCollection getOverlays() {
        return overlayList.getOverlayCollection();
    }

    public final int size() {
        return getOverlays().size();
    }

    public List<PrecalculationOverlay> getGeneratedObjects() {
        return overlayList.getListGeneratedObjects();
    }

    public List<PrecalculationOverlay> getGeneratedObjectsZoomed() {
        return overlayList.getListGeneratedObjectsZoomed();
    }

    private OverlayPrecalculatedCache(
            PrecalculatedOverlayList overlayList, double zoomFactor, Dimensions dim) {
        this.overlayList = overlayList;
        overlayList.assertListsSizeMatch();
        this.zoomFactor = zoomFactor;
        this.dimEntireImage = dim;
    }

    private void rebuildCache() throws CreateException {
        overlayList.rebuild(dimEntireImage, drawOverlay);
        zoomFactor = -1;
        rTree = null;
    }

    /**
     * Gets a scaled overlay if it can from the cache for a particular object. Otherwise creates a
     * new one
     *
     * @param zoomFactorNew
     * @param object used as reference to find a corresponding overlay in cache, or else used to
     *     derive one.
     * @param overlay
     * @param index
     * @param dimScaled
     * @return null if rejected
     * @throws OperationFailedException
     */
    private Optional<PrecalculationOverlay> getOrCreateScaledOverlayForObject(
            double zoomFactorNew,
            ObjectWithProperties object,
            Overlay overlay,
            int index,
            Dimensions dimScaled)
            throws OperationFailedException {
        overlayList.assertZoomedExists();
        if (zoomFactorNew == 1) {
            // We can steal from the main object
            return Optional.ofNullable(overlayList.getPrecalculation(index));
        } else if (zoomFactorNew == zoomFactor) {
            // Great, we can steal the object from the cache, if it's non null
            PrecalculationOverlay omScaledCache = overlayList.getPrecalculationZoomed(index);

            if (omScaledCache != null) {
                return Optional.of(omScaledCache);
            }
        }
        overlayList.assertZoomedExists();
        assert (zoomFactorNew > 0);
        try {

            ObjectWithProperties omScaledProps =
                    overlay.createScaleObject(
                            drawOverlay,
                            zoomFactorNew,
                            object,
                            overlay,
                            dimEntireImage,
                            dimScaled,
                            bvOut);

            // If the object-mask we make from the overlay has no pixels, then we reject it by
            // returning
            // null
            if (!omScaledProps.withoutProperties().voxelsOn().anyExists()) {
                return Optional.empty();
            }

            overlayList.assertZoomedExists();
            // We precalculate this
            PrecalculationOverlay precalculation =
                    DrawOverlay.createPrecalc(drawOverlay, omScaledProps, dimEntireImage);
            overlayList.assertZoomedExists();
            overlayList.setPrecalculationZoomed(index, precalculation);
            return Optional.of(precalculation);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    private void addedScaledIndexTo(
            int i,
            BoundingBox box,
            BoundingBox boxViewZoomed,
            double zoomFactorNew,
            PrecalculatedOverlayList addTo)
            throws OperationFailedException {

        // Our main object
        PrecalculationOverlay originalSize = overlayList.getPrecalculation(i);

        RGBColor col = overlayList.getColor(i);
        Overlay ol = overlayList.getOverlay(i);

        // We scale our object, retrieving from the cache if we can
        Optional<PrecalculationOverlay> scaled =
                getOrCreateScaledOverlayForObject(
                        zoomFactorNew, originalSize.getFirst(), ol, i, dimScaled);

        // We check that our zoomed-version also has an intersection, as sometimes it doesn't
        if (scaled.isPresent()
                && scaled.get()
                        .getFirst()
                        .withoutProperties()
                        .boundingBox()
                        .intersection()
                        .existsWith(boxViewZoomed)) {

            // Previously, we duplicated color here, now we don't
            addTo.add(ol, col, originalSize, box, scaled);
        }
    }

    private Dimensions createDimensionsScaled(double zoomFactorNew) {
        // We create a scaled version of our dimensions
        return dimEntireImage.scaleXYBy(new ScaleFactor(zoomFactorNew));
    }

    /**
     * Creates an r-tree with the indices of each item from a list.
     *
     * @param boxes added to the r-tree, with the index of the element in the stream as the
     *     corresponding item
     * @param maxNumberEntriesSuggested suggested a maximum number of entries in the r-tree
     */
    private static RTree<Integer> createRTreeOfIndices(
            List<BoundingBox> boxes, int maxNumberEntriesSuggested) {
        RTree<Integer> tree = new RTree<>(maxNumberEntriesSuggested);

        for (int i = 0; i < boxes.size(); i++) {
            tree.add(boxes.get(i), i);
        }

        return tree;
    }
}
