/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowThickBorderAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = -163506205817469710L;

    public ShowThickBorderAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Thick Border",
                icon,
                lastMarkDisplaySettings.isShowThickBorder(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowThickBorder(currentState);
        sendable.setShowThickBorder(currentState);
    }
}
