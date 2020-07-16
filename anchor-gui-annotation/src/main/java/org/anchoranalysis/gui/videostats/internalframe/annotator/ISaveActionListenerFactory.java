/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

public interface ISaveActionListenerFactory {

    ActionListener saveFinished(JInternalFrame frame);

    ActionListener savePaused(JComponent dialogParent);

    ActionListener skipAnnotation(JInternalFrame frame);
}
