/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowInsideAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = -9105081219776936274L;

    public ShowInsideAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Inside",
                icon,
                lastMarkDisplaySettings.isShowInside(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowInside(currentState);
        sendable.setShowInside(currentState);
    }
}
