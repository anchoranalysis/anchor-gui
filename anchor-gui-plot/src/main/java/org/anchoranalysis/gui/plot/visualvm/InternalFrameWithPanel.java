/* (C)2020 */
package org.anchoranalysis.gui.plot.visualvm;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

class InternalFrameWithPanel {

    private JInternalFrame frame;

    public InternalFrameWithPanel(String title, JPanel panel) {

        frame = new JInternalFrame(title);

        frame.setResizable(true);
        frame.setMaximizable(true);
        frame.setIconifiable(true);
        frame.setClosable(true);

        // add it to our application
        frame.setContentPane(panel);
    }

    public JInternalFrame getFrame() {
        return frame;
    }
}
