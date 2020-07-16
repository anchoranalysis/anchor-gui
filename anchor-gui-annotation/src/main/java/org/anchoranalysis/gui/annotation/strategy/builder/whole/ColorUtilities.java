/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import java.awt.Color;
import javax.swing.JButton;
import org.anchoranalysis.io.bean.color.RGBColorBean;

class ColorUtilities {

    public static void maybeAddColor(JButton button, RGBColorBean color) {
        if (color != null) {
            addColor(button, color.toAWTColor());
        }
    }

    public static void addColor(JButton button, Color col) {
        button.setBackground(col);
        button.setForeground(getContrastColor(col));
    }

    private static Color getContrastColor(Color col) {
        double lum =
                (((0.299 * col.getRed()) + ((0.587 * col.getGreen()) + (0.114 * col.getBlue()))));
        return lum > 186 ? Color.BLACK : Color.WHITE;
    }
}
