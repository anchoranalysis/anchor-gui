/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

class FrameClosedListener extends InternalFrameAdapter {

    private SaveMonitor saveMonitor;

    public FrameClosedListener(SaveMonitor saveMonitor) {
        super();
        this.saveMonitor = saveMonitor;
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        super.internalFrameClosing(e);

        if (!saveMonitor.isChangedSinceLastSave()) {

            e.getInternalFrame().setVisible(false);
            e.getInternalFrame().dispose();
            return;
        }

        // You can still stop closing if you want to
        int res =
                JOptionPane.showConfirmDialog(
                        e.getInternalFrame(),
                        "There are unsaved changes. Are you sure you want to close?",
                        "Close without saving?",
                        JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            e.getInternalFrame().setVisible(false);
            e.getInternalFrame().dispose();
        }
    }
}
