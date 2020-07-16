/* (C)2020 */
package org.anchoranalysis.gui.cfgnrg;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class StatePanelUpdateException extends AnchorCheckedException {

    private static final long serialVersionUID = 8514917190449094812L;

    public StatePanelUpdateException(String string) {
        super(string);
    }

    public StatePanelUpdateException(Exception exc) {
        super(exc);
    }
}
