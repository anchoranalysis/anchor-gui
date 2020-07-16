/* (C)2020 */
package org.anchoranalysis.gui.videostats.frame;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

class ExitAction extends AbstractAction {

    private static final long serialVersionUID = 6108379470565819605L;

    public ExitAction() {
        super("Exit");
        // putValue(MNEMONIC_KEY, "x");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        ExitUtilities.exit();
    }
}
