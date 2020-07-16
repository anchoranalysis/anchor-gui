/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DisplayStackViewportUtilities {

    public static Point2i clipToImage(
            Point2i val, Extent canvasExtent, ImageDimensions dimensions) {
        return new Point2i(
                clipToImage(val.getX(), dimensions.getX(), canvasExtent.getX()),
                clipToImage(val.getY(), dimensions.getY(), canvasExtent.getY()));
    }

    private static int clipToImage(int val, int entireExtent, int canvasWidth) {

        if (val < 0) {
            return 0;
        }

        int furthestXVal = Math.max(entireExtent - canvasWidth, 0);
        if (val > furthestXVal) {
            return furthestXVal;
        }

        return val;
    }
}
