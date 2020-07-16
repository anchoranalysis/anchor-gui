/* (C)2020 */
package org.anchoranalysis.gui.frame.details;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class StackChangedOrMouseExited extends MouseAdapter implements ChangeListener {

    private StringHelper stringConstructor;
    private JLabel detailsLabel;

    public StackChangedOrMouseExited(StringHelper stringConstructor, JLabel detailsLabel) {
        super();
        this.stringConstructor = stringConstructor;
        this.detailsLabel = detailsLabel;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        updateLabelNoPosition();
    }

    @Override
    public void stateChanged(ChangeEvent arg0) {
        updateLabelNoPosition();
    }

    private void updateLabelNoPosition() {
        StringBuilder sb = new StringBuilder();
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
