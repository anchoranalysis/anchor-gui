/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import java.awt.Color;
import javax.swing.JInternalFrame;
import org.anchoranalysis.gui.mdi.MDIDesktopPane;
import org.anchoranalysis.gui.mdi.Usher;

// The desktop pane we use for showing our video stats
public class VideoStatsDesktopPane extends MDIDesktopPane {

    private static final long serialVersionUID = 6717960064924893995L;

    public VideoStatsDesktopPane() {
        super();

        setBackground(Color.WHITE);

        addContainerListener(new Usher());
    }

    public void addInternalFrame(JInternalFrame internalFrame) {
        add(internalFrame);
    }
}
