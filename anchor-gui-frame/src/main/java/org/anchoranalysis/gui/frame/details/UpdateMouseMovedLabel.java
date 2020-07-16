/* (C)2020 */
package org.anchoranalysis.gui.frame.details;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JLabel;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;

class UpdateMouseMovedLabel extends MouseMotionAdapter {

    private StringHelper stringConstructor;
    private JLabel detailsLabel;
    private InternalFrameCanvas internalFrameCanvas;

    public UpdateMouseMovedLabel(
            StringHelper stringConstructor,
            JLabel detailsLabel,
            InternalFrameCanvas internalFrameCanvas) {
        super();
        this.stringConstructor = stringConstructor;
        this.detailsLabel = detailsLabel;
        this.internalFrameCanvas = internalFrameCanvas;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        if (internalFrameCanvas.canvasContainsAbs(e.getX(), e.getY())) {
            updateLabelInside(
                    e.getX(), e.getY(), internalFrameCanvas.intensityStrAtAbs(e.getX(), e.getY()));
        } else {
            updateLabelOutside(e.getX(), e.getY());
        }
    }

    private void updateLabelOutside(int x, int y) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConstructor.posString(x, y));
        sb.append(" ");
        sb.append(stringConstructor.zoomString());
        sb.append(" ");
        sb.append(stringConstructor.typeString());
        sb.append(" ");
        sb.append(stringConstructor.genResString());
        sb.append(" ");
        sb.append(stringConstructor.extraString());
        detailsLabel.setText(sb.toString());
    }

    private void updateLabelInside(int x, int y, String intensityStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(stringConstructor.posString(x, y));
        sb.append(" ");
        sb.append(intensityStr);
        sb.append(" ");
        sb.append(stringConstructor.zoomString());
        sb.append(" ");
        sb.append(stringConstructor.typeString());
        sb.append(" ");
        sb.append(stringConstructor.genResString());
        sb.append(" ");
        sb.append(stringConstructor.extraString());
        detailsLabel.setText(sb.toString());
    }
}
