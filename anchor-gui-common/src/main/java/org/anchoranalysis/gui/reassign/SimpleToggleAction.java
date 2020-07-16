/* (C)2020 */
package org.anchoranalysis.gui.reassign;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class SimpleToggleAction extends AbstractAction {

    private static final long serialVersionUID = 340313293522185354L;
    /** */
    public SimpleToggleAction(String title, boolean defaultState) {
        super(title);
        putValue(Action.SELECTED_KEY, defaultState);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {}

    public void setToggleState(boolean toggleState) {
        putValue(Action.SELECTED_KEY, toggleState);
    }

    public boolean isToggleState() {
        return (Boolean) getValue(Action.SELECTED_KEY);
    }
}
