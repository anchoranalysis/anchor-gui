/* (C)2020 */
package org.anchoranalysis.gui.reassign;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

public class JDropdownButton {

    private JButton button;

    private JPopupMenu popup;

    public JDropdownButton(String name, ImageIcon icon) {

        popup = new JPopupMenu();

        button = new JButton(name, icon);
        // add(button);

        button.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        popup.show(button, 0, button.getHeight());
                    }
                });
    }

    public int getNumItems() {
        return popup.getSubElements().length;
    }

    public JButton getButton() {
        return button;
    }

    public JPopupMenu getPopup() {
        return popup;
    }
}
