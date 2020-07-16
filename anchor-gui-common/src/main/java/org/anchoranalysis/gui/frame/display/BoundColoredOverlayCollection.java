/* (C)2020 */
package org.anchoranalysis.gui.frame.display;

import java.util.List;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributesFactory;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.overlay.OverlayPrecalculatedCache;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class BoundColoredOverlayCollection {

    private DrawOverlay maskWriter;

    private IDGetter<Overlay> idGetter;

    private ImageDimensions dimEntireImage;

    // The current overlay, with additional cached objects
    private OverlayPrecalculatedCache cache;

    public BoundColoredOverlayCollection(
            DrawOverlay maskWriter, IDGetter<Overlay> idGetter, ImageDimensions dim)
            throws CreateException {
        super();
        this.maskWriter = maskWriter;
        this.idGetter = idGetter;
        this.dimEntireImage = dim;
        this.cache =
                new OverlayPrecalculatedCache(
                        new ColoredOverlayCollection(), dimEntireImage, maskWriter);
    }

    public void updateMaskWriter(DrawOverlay maskWriter) throws SetOperationFailedException {
        this.maskWriter = maskWriter;
        this.cache.setMaskWriter(maskWriter);
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

    public void drawRGB(RGBStack stack, BoundingBox bbox, double zoomFactor)
            throws OperationFailedException {

        // Create a containing bounding box with the zoom
        BoundingBox container =
                createZoomedContainer(bbox, zoomFactor, stack.getDimensions().getExtent());

        OverlayPrecalculatedCache marksWithinView =
                cache.subsetWithinView(bbox, container, zoomFactor);

        maskWriter.writePrecalculatedOverlays(
                marksWithinView.getGeneratedObjectsZoomed(),
                dimEntireImage,
                stack,
                ObjectDrawAttributesFactory.createFromOverlays(
                        marksWithinView.getOverlays(), idGetter, new IDGetterIter<>()),
                container);
    }

    private static BoundingBox createZoomedContainer(
            BoundingBox bbox, double zoomFactor, Extent stackExtent) {
        Point3i cornerMin = new Point3i(bbox.cornerMin());
        cornerMin.scaleXY(zoomFactor);
        return new BoundingBox(cornerMin, stackExtent);
    }

    // Note the overlay do not actually have to be contained in the OverlayCollection for this to
    // work
    //  it will work with any overlay.... simply using the settings from the bound maskWriter
    public List<BoundingBox> bboxList(ColoredOverlayCollection oc) {
        return oc.bboxList(maskWriter, dimEntireImage);
    }

    public List<BoundingBox> bboxList(OverlayCollection oc) {
        return oc.bboxList(maskWriter, dimEntireImage);
    }

    public synchronized OverlayPrecalculatedCache getPrecalculatedCache() {
        return cache;
    }
}
