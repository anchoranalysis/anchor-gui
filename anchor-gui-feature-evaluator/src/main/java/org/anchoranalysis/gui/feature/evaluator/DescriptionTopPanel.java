/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.gui.reassign.SimpleToggleAction;

public class DescriptionTopPanel {

    private JTextPane cfgDescriptionTop;

    private JPanel panelTop;

    private SimpleToggleAction toggleActionFreeze;

    public DescriptionTopPanel() {

        toggleActionFreeze = new SimpleToggleAction("Freeze", false);

        cfgDescriptionTop = new JTextPane();
        cfgDescriptionTop.setEditable(false);

        panelTop = new JPanel();
        panelTop.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        panelTop.setLayout(new BorderLayout());
        panelTop.add(new JToggleButton(toggleActionFreeze), BorderLayout.WEST);
        panelTop.add(cfgDescriptionTop, BorderLayout.CENTER);
    }

    public void updateDescriptionTop(OverlayCollection overlays) {

        if (overlays != null) {
            cfgDescriptionTop.setText(
                    "selected ids: " + new IndicesSelection(overlays.integerSet()).toString());
        } else {
            cfgDescriptionTop.setText("no selection");
        }
    }

    public JPanel getPanel() {
        return panelTop;
    }

    public boolean isFrozen() {
        return toggleActionFreeze.isToggleState();
    }
}
