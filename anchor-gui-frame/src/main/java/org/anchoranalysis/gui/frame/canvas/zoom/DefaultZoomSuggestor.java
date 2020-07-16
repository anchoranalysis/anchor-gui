/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas.zoom;

import lombok.AllArgsConstructor;
import org.anchoranalysis.image.extent.ImageDimensions;

@AllArgsConstructor
public class DefaultZoomSuggestor {

    private final int maxX;
    private final int maxY;

    public ZoomScale suggestDefaultZoomFor(ImageDimensions dimensions) {
        int maxExpX = getMaxExpForDim(maxX, dimensions.getX());
        int maxExpY = getMaxExpForDim(maxY, dimensions.getY());
        int exp = Math.min(maxExpX, maxExpY);
        return new ZoomScale(exp);
    }

    private static int getMaxExpForDim(int maxWidthAllowed, int currentWidth) {

        double ratio = ((double) maxWidthAllowed) / currentWidth;
        double log = Math.log(ratio) / Math.log(2);
        return (int) Math.floor(log);
    }
}
