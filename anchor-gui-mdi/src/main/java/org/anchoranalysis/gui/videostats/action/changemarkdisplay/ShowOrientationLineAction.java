/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowOrientationLineAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = 7074968500999931478L;

    public ShowOrientationLineAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Orientation Line",
                icon,
                lastMarkDisplaySettings.isShowOrientationLine(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowOrientationLine(currentState);
        sendable.setShowOrientationLine(currentState);
    }
}
