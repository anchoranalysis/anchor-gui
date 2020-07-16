/* (C)2020 */
package org.anchoranalysis.gui.frame.details;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;

class ZoomInAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    private InternalFrameCanvas internalFrameCanvas;

    public ZoomInAction(InternalFrameCanvas internalFrameCanvas) {
        super("", new IconFactory().icon("/dialogIcon/zoom_in.png"));
        this.internalFrameCanvas = internalFrameCanvas;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        internalFrameCanvas.zoomIn();
    }

    public void dispose() {
        internalFrameCanvas = null;
    }
}
