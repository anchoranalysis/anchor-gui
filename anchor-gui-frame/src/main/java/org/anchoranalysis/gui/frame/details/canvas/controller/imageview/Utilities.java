/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas.controller.imageview;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import org.anchoranalysis.image.extent.Extent;

class Utilities {

    public static Extent addXY(Extent e, int addX, int addY) {
        return new Extent(e.getX() + addX, e.getY() + addY, e.getZ());
    }

    public static Extent fractionScreenBounds(
            double widthFractionScreen,
            double heightFractionScreen,
            GraphicsConfiguration graphicsConfiguration) {
        Rectangle screenBounds = graphicsConfiguration.getBounds();

        int desiredWidth = intFraction(screenBounds.getWidth(), widthFractionScreen);
        int desiredHeight = intFraction(screenBounds.getHeight(), heightFractionScreen);
        return new Extent(desiredWidth, desiredHeight, 0);
    }

    private static int intFraction(double total, double fraction) {
        return (int) (total * fraction);
    }
}
