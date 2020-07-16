/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import javax.swing.JComponent;

public interface ControllerOrder {

    void setAsTopComponent(JComponent component);

    // SHOULD BE CALLED BEFORE INIT
    void setAsBottomComponent(JComponent component);
}
