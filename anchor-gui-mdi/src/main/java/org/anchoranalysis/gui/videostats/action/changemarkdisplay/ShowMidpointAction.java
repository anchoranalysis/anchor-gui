/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowMidpointAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = -9105081219776936274L;

    public ShowMidpointAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Midpoint",
                icon,
                lastMarkDisplaySettings.isShowMidpoint(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowMidpoint(currentState);
        sendable.setShowMidpoint(currentState);
    }
}
