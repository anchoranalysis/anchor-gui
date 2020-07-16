/* (C)2020 */
package org.anchoranalysis.gui.frame.details;

import javax.swing.JMenu;

public interface ControllerPopupMenu {

    void addAdditionalMenu(JMenu menu);

    void setRetrieveElementsInPopupEnabled(boolean retrieveElementsInPopupEnabled);
}
