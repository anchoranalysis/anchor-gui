/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.error.AnchorCheckedException;

public class MenuAddException extends AnchorCheckedException {

    private static final long serialVersionUID = 6739639594891484534L;

    public MenuAddException(String string) {
        super(string);
    }

    public MenuAddException(Exception exc) {
        super(exc);
    }
}
