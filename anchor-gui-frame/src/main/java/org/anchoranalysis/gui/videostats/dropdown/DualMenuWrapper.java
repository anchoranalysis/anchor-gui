/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuListener;

// Allows us to do stuff to either a Menu or MenuPopup depending on which was picked
public class DualMenuWrapper {

    private JMenu menu = null;
    private JPopupMenu menuPopup = null;

    public DualMenuWrapper(JMenu menu) {
        super();
        this.menu = menu;
    }

    // ROOT
    public DualMenuWrapper(JPopupMenu menu) {
        super();
        this.menuPopup = menu;
    }

    public void add(JMenuItem item) {

        if (menu != null) {
            this.menu.add(item);
        }

        if (menuPopup != null) {
            this.menuPopup.add(item);
        }
    }

    public void addSeparator() {

        if (menu != null) {
            this.menu.addSeparator();
        }

        if (menuPopup != null) {
            this.menuPopup.addSeparator();
        }
    }

    public void addMenuListener(MenuListener l) {

        if (menu != null) {
            this.menu.addMenuListener(l);
        }

        // NO menu listener for menuPopUp
        if (menuPopup != null) {
            assert false;
        }
    }

    public void addSeperator() {

        if (menu != null) {
            this.menu.addSeparator();
        }

        if (menuPopup != null) {
            this.menuPopup.addSeparator();
        }
    }

    public String getText() {

        if (menu != null) {
            return menu.getText();
        }

        if (menuPopup != null) {
            return menuPopup.getLabel();
        }

        assert false;
        return "";
    }
}
