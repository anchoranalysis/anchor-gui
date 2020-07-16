/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe;

import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;

// Makes multile proposals in a loop until success, and allows the user
//  to configure-this and get feedback
public class ProposeLoopPanel {

    private JPanel panel;

    // If selected, we keep on looping until we get a successful proposal
    private JToggleButton buttonToggleLoopUntilProposal;

    private JTextField textMaxNumTries;
    private JTextField textLastNumTries;

    private static JPanel createLabelledComponent(String label, JComponent component) {

        JPanel panelLocal = new JPanel();
        panelLocal.add(new JLabel(label));
        panelLocal.add(component);
        return panelLocal;
    }

    public ProposeLoopPanel() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        buttonToggleLoopUntilProposal = new JToggleButton("Loop until Proposal");
        buttonToggleLoopUntilProposal.setSelected(false);

        panel.add(buttonToggleLoopUntilProposal);

        {
            textMaxNumTries = new JTextField(5);
            textMaxNumTries.setText("100");
            JPanel panelLocal = createLabelledComponent("Max Number Tries", textMaxNumTries);
            panel.add(panelLocal);
        }

        {
            textLastNumTries = new JTextField(5);
            textLastNumTries.setEditable(false);
            JPanel panelLocal = createLabelledComponent("Number Tries", textLastNumTries);
            panel.add(panelLocal);
        }
    }

    public ProposedCfg propose(ProposalOperation proposable, ErrorNode errorNode)
            throws ProposalAbnormalFailureException {

        int maxNumLoopsUntilProposal = Integer.valueOf(textMaxNumTries.getText());

        ProposedCfg succ;

        int i = 0;
        while (true) {

            i++;

            // Now we let the mark proposer do it's work
            succ = proposable.propose(errorNode.addFormatted("Try %d", i));

            // If successful
            if (succ.isSuccess()) {
                break;
            }

            // If we are not in loop-mode
            if (!buttonToggleLoopUntilProposal.isSelected()) {
                break;
            }

            // If we've reached our max
            if (i == maxNumLoopsUntilProposal) {
                break;
            }
        }

        textLastNumTries.setText(String.valueOf(i));

        return succ;
    }

    public JPanel getPanel() {
        return panel;
    }
}
