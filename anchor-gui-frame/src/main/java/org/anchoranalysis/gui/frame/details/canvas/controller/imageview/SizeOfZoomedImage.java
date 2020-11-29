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

package org.anchoranalysis.gui.frame.details.canvas.controller.imageview;

import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.addXY;
import static org.anchoranalysis.gui.frame.details.canvas.controller.imageview.Utilities.fractionScreenBounds;

import java.awt.GraphicsConfiguration;
import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.spatial.Extent;

class SizeOfZoomedImage {

    public static Extent apply(
            double widthFractionScreen,
            double heightFractionScreen,
            int zoomWidthSubtract,
            int zoomHeightSubtract,
            GraphicsConfiguration graphicsConfiguration,
            Dimensions imageSize) {
        Extent e =
                fractionScreenBounds(
                        widthFractionScreen, heightFractionScreen, graphicsConfiguration);
        Extent eAdjusted = addXY(e, -zoomWidthSubtract, -zoomHeightSubtract);
        return sizeWithinBounds(imageSize, eAdjusted);
    }

    /** The size of a zoomed-image after being zoomed to fit within bounds */
    private static Extent sizeWithinBounds(Dimensions imageSize, Extent maxBounds) {
        DefaultZoomSuggestor zoomSugg = new DefaultZoomSuggestor(maxBounds.x(), maxBounds.y());
        ZoomScale zs = zoomSugg.suggestDefaultZoomFor(imageSize);

        // The size of the image after it has been zoomed
        return zs.applyScale(imageSize.extent());
    }
}
