/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowSolidAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = -163506205817469710L;

    public ShowSolidAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Solid Area",
                icon,
                lastMarkDisplaySettings.isShowSolid(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowSolid(currentState);
        sendable.setShowSolid(currentState);
    }
}
