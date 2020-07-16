/* (C)2020 */
package org.anchoranalysis.gui.mdi.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mdi.MDIDesktopPane;

public class CascadeAction extends AbstractAction {

    private static final long serialVersionUID = 7670262976708839019L;
    private MDIDesktopPane desktopPane;

    public CascadeAction(MDIDesktopPane desktopPane, ImageIcon icon) {
        super("", icon);
        putValue(SHORT_DESCRIPTION, "Cascade");
        // putValue(MNEMONIC_KEY, mnemonic);

        this.desktopPane = desktopPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        desktopPane.cascadeFrames();
    }
}
