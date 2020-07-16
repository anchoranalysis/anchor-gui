/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameListener;

public interface ControllerFrame {

    void setUseSplitPlane(boolean use);

    void addInternalFrameListener(InternalFrameListener l);

    void setDefaultCloseOperation(int operation);

    JInternalFrame getFrame();
}
