/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.anchoranalysis.gui.frame.canvas.ImageCanvas;

class FocusRequester extends MouseAdapter {

    private ImageCanvas canvas;

    public FocusRequester(ImageCanvas canvas) {
        super();
        this.canvas = canvas;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        canvas.requestFocusInWindow();
    }
}
