/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
