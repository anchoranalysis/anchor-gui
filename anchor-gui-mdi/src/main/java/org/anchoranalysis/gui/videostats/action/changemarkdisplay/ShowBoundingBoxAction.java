/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

import java.util.List;
import javax.swing.ImageIcon;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;

public class ShowBoundingBoxAction extends ChangeMarkDisplayToggleAction {

    private static final long serialVersionUID = -3278163486131286604L;

    public ShowBoundingBoxAction(
            List<IChangeMarkDisplaySendable> updateList,
            MarkDisplaySettings lastMarkDisplaySettings,
            ImageIcon icon) {
        super(
                "Show Bounding Box",
                icon,
                lastMarkDisplaySettings.isShowBoundingBox(),
                updateList,
                lastMarkDisplaySettings);
    }

    @Override
    protected void changeMarkDisplay(IChangeMarkDisplaySendable sendable, boolean currentState) {

        getLastMarkDisplaySettings().setShowBoundingBox(currentState);
        sendable.setIncludeBoundingBox(currentState);
    }
}
