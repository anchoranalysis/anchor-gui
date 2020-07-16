/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public abstract class ChangeMarkDisplayToggleAction extends AbstractAction {

    private static final long serialVersionUID = -4367824021242290791L;

    private List<IChangeMarkDisplaySendable> updateList;

    private MarkDisplaySettings lastMarkDisplaySettings;

    public ChangeMarkDisplayToggleAction(
            String title,
            ImageIcon icon,
            boolean defaultState,
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings) {
        super("", icon);
        this.updateList = updateList;
        this.lastMarkDisplaySettings = lastMarkDisplaySettings;
        putValue(Action.SELECTED_KEY, defaultState);
        putValue(SHORT_DESCRIPTION, title);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        for (IChangeMarkDisplaySendable sendable : updateList) {
            changeMarkDisplay(sendable, (Boolean) getValue(Action.SELECTED_KEY));
        }
    }

    protected MarkDisplaySettings getLastMarkDisplaySettings() {
        return lastMarkDisplaySettings;
    }

    protected abstract void changeMarkDisplay(
            IChangeMarkDisplaySendable sendable, boolean currentState);
}
