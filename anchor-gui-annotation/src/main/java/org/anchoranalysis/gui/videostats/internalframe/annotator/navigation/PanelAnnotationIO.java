/*-
 * #%L
 * anchor-gui-annotation
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

package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import org.anchoranalysis.gui.videostats.internalframe.annotator.ISaveActionListenerFactory;

// A panel for selecting tools
public class PanelAnnotationIO extends PanelWithLabel {

    private JButton buttonSkipAnnotation;
    private JButton buttonSaveUnfinished; // Save to return to later
    private JButton buttonSaveFinished;

    public PanelAnnotationIO() {
        buttonSkipAnnotation = new JButton("Skip");
        buttonSaveUnfinished = new JButton("Save");
        buttonSaveFinished = new JButton("Finish");
        super.init("Annotation");

        buttonSkipAnnotation.setFocusable(false);
        buttonSaveUnfinished.setFocusable(false);
        buttonSaveFinished.setFocusable(false);
    }

    public void addActionsToSavePanel(ISaveActionListenerFactory factory, JInternalFrame frame) {

        addActionListenerSaveFinished(factory.saveFinished(frame));
        addActionListenerSaveUnfinished(factory.savePaused(frame));
        addActionListenerSkipAnnotation(factory.skipAnnotation(frame));
    }

    @Override
    protected JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));

        panel.add(buttonSkipAnnotation);
        panel.add(buttonSaveUnfinished);
        panel.add(buttonSaveFinished);
        return panel;
    }

    private void addActionListenerSaveUnfinished(ActionListener l) {
        buttonSaveUnfinished.addActionListener(l);
    }

    private void addActionListenerSaveFinished(ActionListener l) {
        buttonSaveFinished.addActionListener(l);
    }

    private void addActionListenerSkipAnnotation(ActionListener l) {
        buttonSkipAnnotation.addActionListener(l);
    }
}
