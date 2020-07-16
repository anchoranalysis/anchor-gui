/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameListener;

class InternalFrameDelegate {

    private JInternalFrame delegate;

    public InternalFrameDelegate(
            String title,
            boolean resizable,
            boolean closable,
            boolean maximizable,
            boolean iconifiable) {
        delegate = new JInternalFrame(title, resizable, closable, maximizable, iconifiable);
    }

    public Dimension getMinimumSize() {
        return delegate.getContentPane().getMinimumSize();
    }

    public Dimension getPreferredSize() {
        Dimension d = delegate.getContentPane().getPreferredSize();
        // System.out.printf("Frame pref size=%f,%f\n",
        // frame.getContentPane().getPreferredSize().getWidth(),
        // frame.getContentPane().getPreferredSize().getHeight() );
        return new Dimension(d.width + 10, d.height + 40);
    }

    public void dispose() {

        InternalFrameListener[] list = delegate.getInternalFrameListeners();

        for (InternalFrameListener item : list) {
            delegate.removeInternalFrameListener(item);
        }

        delegate.dispose();
        delegate = null;
    }

    public void setMaximumSize(Dimension maximumSize) {
        delegate.setMaximumSize(maximumSize);
    }

    public void setSize(int width, int height) {
        delegate.setSize(width, height);
    }

    public void setVisible(boolean aFlag) {
        delegate.setVisible(aFlag);
    }

    public Container getContentPane() {
        return delegate.getContentPane();
    }

    public void setTitle(String title) {
        delegate.setTitle(title);
    }

    public void setDefaultCloseOperation(int operation) {
        delegate.setDefaultCloseOperation(operation);
    }

    public void add(Component comp, Object constraints) {
        delegate.add(comp, constraints);
    }

    public void addInternalFrameListener(InternalFrameListener l) {
        delegate.addInternalFrameListener(l);
    }

    public JInternalFrame getFrame() {
        return delegate;
    }
}
