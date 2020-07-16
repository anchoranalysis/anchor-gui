/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class PanelWithLabel {

    private JPanel delegate = new JPanel();
    private JLabel label;

    public void init(String labelText) {
        delegate.setLayout(new GridBagLayout());

        this.label = new JLabel(labelText, JLabel.CENTER);

        addComponent(label, 0, GridBagConstraints.HORIZONTAL);
        addComponent(createMainPanel(), 1, GridBagConstraints.BOTH);
    }

    protected abstract JPanel createMainPanel();

    public JComponent getPanel() {
        return delegate;
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public void setLabelForeground(Color fg) {
        label.setForeground(fg);
    }

    private void addComponent(JComponent componentToAdd, int gridy, int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = gridy;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        delegate.add(componentToAdd, c);
    }
}
