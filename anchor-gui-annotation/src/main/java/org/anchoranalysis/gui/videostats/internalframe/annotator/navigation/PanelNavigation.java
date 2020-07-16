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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class PanelNavigation {

    private JPanel panel = new JPanel();

    private PanelWithLabel panelWithError;

    public PanelNavigation(PanelWithLabel singlePanel) {
        super();
        commonConstructorSetup(singlePanel);

        addPanel(singlePanel.getPanel(), 0, GridBagConstraints.CENTER);
    }

    /**
     * A navigigation panel with a left, middle, and right panel
     *
     * @param leftPanel
     * @param middlePanel
     * @param rightPanel
     */
    public PanelNavigation(
            JComponent leftPanel, PanelWithLabel middlePanel, JComponent rightPanel) {
        super();
        commonConstructorSetup(middlePanel);

        addPanel(leftPanel, 0, GridBagConstraints.LINE_START);
        addPanel(middlePanel.getPanel(), 1, GridBagConstraints.CENTER);
        addPanel(rightPanel, 2, GridBagConstraints.LINE_END);
    }

    private void commonConstructorSetup(PanelWithLabel panelWithError) {
        panel.setLayout(new GridBagLayout());
        this.panelWithError = panelWithError;
    }

    public JComponent getPanel() {
        return panel;
    }

    public void setErrorLabelText(String text) {
        panelWithError.setLabelText(text);
    }

    public void dispose() {
        getPanel().removeAll();
        panelWithError = null;
    }

    private void addPanel(JComponent panelToAdd, int gridx, int anchor) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = 0;
        c.anchor = anchor;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;

        panel.add(panelToAdd, c);
    }
}
