/* (C)2020 */
package org.anchoranalysis.gui.file.opened;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;

public interface IOpenedFileGUI {

    VideoStatsOperationMenu getRootMenu();

    JButton getButton();

    JPopupMenu getPopup();

    void executeDefaultOperation();
}
