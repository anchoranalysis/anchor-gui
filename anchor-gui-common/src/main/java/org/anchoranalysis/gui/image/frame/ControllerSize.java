/* (C)2020 */
package org.anchoranalysis.gui.image.frame;

import java.awt.Dimension;

public abstract class ControllerSize {

    public void configureSize(int width, int height) {
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim);
        setMinimumSize(dim);
    }

    /** Sets the minimum and preferred to the same size */
    public void configureSize(
            int minimumWidth, int minimumHeight, int preferredWidth, int preferredHeight) {
        setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        setMinimumSize(new Dimension(minimumWidth, minimumHeight));
    }

    protected abstract void setMinimumSize(Dimension minimumSize);

    protected abstract void setPreferredSize(Dimension preferredSize);
}
