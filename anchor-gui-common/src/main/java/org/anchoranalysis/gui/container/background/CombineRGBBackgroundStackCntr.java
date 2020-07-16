/* (C)2020 */
package org.anchoranalysis.gui.container.background;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;

public class CombineRGBBackgroundStackCntr {

    private BackgroundStackCntr red;
    private BackgroundStackCntr green;
    private BackgroundStackCntr blue;

    public BackgroundStackCntr create() throws GetOperationFailedException, InitException {

        final CombineRGBBoundedIndexContainer combinedCntr = new CombineRGBBoundedIndexContainer();

        assert (red != null || green != null || blue != null);

        if (red != null) {
            combinedCntr.setRed(red.backgroundStackCntr());
        }

        if (green != null) {
            combinedCntr.setGreen(green.backgroundStackCntr());
        }

        if (blue != null) {
            combinedCntr.setBlue(blue.backgroundStackCntr());
        }

        combinedCntr.init();

        return new SingleBackgroundStackCntr(combinedCntr);
    }

    public BackgroundStackCntr getRed() {
        return red;
    }

    public void setRed(BackgroundStackCntr red) {
        this.red = red;
    }

    public BackgroundStackCntr getGreen() {
        return green;
    }

    public void setGreen(BackgroundStackCntr green) {
        this.green = green;
    }

    public BackgroundStackCntr getBlue() {
        return blue;
    }

    public void setBlue(BackgroundStackCntr blue) {
        this.blue = blue;
    }
}
