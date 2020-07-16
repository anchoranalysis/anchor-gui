/* (C)2020 */
package org.anchoranalysis.gui.mdi.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mdi.MDIDesktopPane;

public class TileAction extends AbstractAction {

    private static final long serialVersionUID = -6489545779257029397L;
    private MDIDesktopPane desktopPane;

    public TileAction(MDIDesktopPane desktopPane, ImageIcon icon) {
        super("", icon);
        putValue(SHORT_DESCRIPTION, "Tile Frames");
        // putValue(MNEMONIC_KEY, mnemonic);

        this.desktopPane = desktopPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        desktopPane.tileFrames();
    }
}
