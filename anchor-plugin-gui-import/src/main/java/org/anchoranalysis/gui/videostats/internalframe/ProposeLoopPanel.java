/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.internalframe;

import java.awt.FlowLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;

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

    public ProposedMarks propose(ProposalOperation proposable, ErrorNode errorNode)
            throws ProposalAbnormalFailureException {

        int maxNumLoopsUntilProposal = Integer.valueOf(textMaxNumTries.getText());

        ProposedMarks succ;

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
