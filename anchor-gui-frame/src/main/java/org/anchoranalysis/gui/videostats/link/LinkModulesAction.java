/* (C)2020 */
package org.anchoranalysis.gui.videostats.link;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

class LinkModulesAction<PropertyValueType> extends AbstractAction {

    private static final long serialVersionUID = 4270644785528612411L;

    private LinkedPropertyModuleSet<PropertyValueType> linkedModuleSet;

    public LinkModulesAction(
            String title,
            LinkedPropertyModuleSet<PropertyValueType> linkedModuleSet,
            ImageIcon icon) {
        super("", icon);

        this.linkedModuleSet = linkedModuleSet;

        putValue(Action.SELECTED_KEY, true);

        putValue(SHORT_DESCRIPTION, title);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        boolean selected = (Boolean) getValue(Action.SELECTED_KEY);
        linkedModuleSet.setEnabled(selected);
    }
}
