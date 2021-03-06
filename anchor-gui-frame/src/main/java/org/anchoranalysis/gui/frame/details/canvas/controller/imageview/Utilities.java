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

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import org.anchoranalysis.spatial.Extent;

class Utilities {

    public static Extent addXY(Extent e, int addX, int addY) {
        return new Extent(e.x() + addX, e.y() + addY, e.z());
    }

    public static Extent fractionScreenBounds(
            double widthFractionScreen,
            double heightFractionScreen,
            GraphicsConfiguration graphicsConfiguration) {
        Rectangle screenBounds = graphicsConfiguration.getBounds();

        int desiredWidth = intFraction(screenBounds.getWidth(), widthFractionScreen);
        int desiredHeight = intFraction(screenBounds.getHeight(), heightFractionScreen);
        // TODO this looks like the z dimension should be changed to 1. What are the implications?
        return new Extent(desiredWidth, desiredHeight, 0);
    }

    private static int intFraction(double total, double fraction) {
        return (int) (total * fraction);
    }
}
