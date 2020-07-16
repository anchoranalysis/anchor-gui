/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas.controller.imageview;

import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.addXY;
import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.fractionScreenBounds;

import java.awt.GraphicsConfiguration;
import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;

class SizeOfZoomedImage {

    public static Extent apply(
            double widthFractionScreen,
            double heightFractionScreen,
            int zoomWidthSubtract,
            int zoomHeightSubtract,
            GraphicsConfiguration graphicsConfiguration,
            ImageDimensions imageSize) {
        Extent e =
                fractionScreenBounds(
                        widthFractionScreen, heightFractionScreen, graphicsConfiguration);
        Extent eAdjusted = addXY(e, -zoomWidthSubtract, -zoomHeightSubtract);
        return sizeWithinBounds(imageSize, eAdjusted);
    }

    /** The size of a zoomed-image after being zoomed to fit within bounds */
    private static Extent sizeWithinBounds(ImageDimensions imageSize, Extent maxBounds) {
        DefaultZoomSuggestor zoomSugg =
                new DefaultZoomSuggestor(maxBounds.getX(), maxBounds.getY());
        ZoomScale zs = zoomSugg.suggestDefaultZoomFor(imageSize);

        // The size of the image after it has been zoomed
        return zs.applyScale(imageSize.getExtent());
    }
}
