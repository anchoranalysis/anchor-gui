/* (C)2020 */
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
