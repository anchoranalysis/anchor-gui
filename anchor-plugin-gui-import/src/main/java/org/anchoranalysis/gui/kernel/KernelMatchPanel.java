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

package org.anchoranalysis.gui.kernel;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.VoxelizedMarksWithEnergy;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.gui.kernel.match.MatchKernel;
import org.anchoranalysis.gui.kernel.match.MatchKernelAccptd;
import org.anchoranalysis.gui.kernel.match.MatchKernelAllChangedMarksFoundIn;
import org.anchoranalysis.gui.kernel.match.MatchKernelAnd;
import org.anchoranalysis.gui.kernel.match.MatchKernelAny;
import org.anchoranalysis.gui.kernel.match.MatchKernelAnyChangedMarksFoundIn;
import org.anchoranalysis.gui.kernel.match.MatchKernelExecutionTimeGreaterThanEquals;
import org.anchoranalysis.gui.kernel.match.MatchKernelProp;
import org.anchoranalysis.mpp.segment.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.segment.kernel.proposer.WeightedKernel;

public class KernelMatchPanel {

    private JPanel delegate = new JPanel();

    private JComboBox<NameIntValue> comboKernelType;
    private JComboBox<NameIntValue> comboAccepted;
    private JComboBox<NameIntValue> comboProposed;
    private JLabel labelMarks;
    private JComboBox<NameIntValue> comboMarkSelection;
    private JTextField textFieldMinimumExecutionTime;

    private static final int ACCEPTED_ANY = -1;
    private static final int ACCEPTED_ACCEPTED = 1;
    private static final int ACCEPTED_REJECTED = 0;

    private static final int PROPOSED_ANY = -1;
    private static final int PROPOSED_PROPOSED = 1;
    private static final int PROPOSED_NOT_PROPOSED = 0;

    private static final int MARKSELECTION_ANY = 1;
    private static final int MARKSELECTION_ALL = 0;

    public KernelMatchPanel(KernelProposer<VoxelizedMarksWithEnergy> kernelProposer) {

        delegate.setLayout(new GridLayout(6, 2));

        // Combo Type
        {
            comboKernelType = new JComboBox<>();
            comboKernelType.addItem(new NameIntValue("any", -1));

            for (int i = 0; i < kernelProposer.getNumberKernels(); i++) {
                WeightedKernel<VoxelizedMarksWithEnergy> kf =
                        kernelProposer.getAllKernelFactories().get(i);
                comboKernelType.addItem(new NameIntValue(kf.getName(), i));
            }

            delegate.add(new JLabel("Kernel Type"));
            delegate.add(comboKernelType);
        }

        // Accepted or not
        {
            comboAccepted = new JComboBox<>();
            comboAccepted.addItem(new NameIntValue("any", ACCEPTED_ANY));
            comboAccepted.addItem(new NameIntValue("accepted", ACCEPTED_ACCEPTED));
            comboAccepted.addItem(new NameIntValue("rejected", ACCEPTED_REJECTED));

            delegate.add(new JLabel("Accepted"));
            delegate.add(comboAccepted);
        }

        // Proposed or not
        {
            comboProposed = new JComboBox<>();
            comboProposed.addItem(new NameIntValue("any", PROPOSED_ANY));
            comboProposed.addItem(new NameIntValue("proposed", PROPOSED_PROPOSED));
            comboProposed.addItem(new NameIntValue("not proposed", PROPOSED_NOT_PROPOSED));

            delegate.add(new JLabel("Proposed"));
            delegate.add(comboProposed);
        }

        // Selected marks
        {
            labelMarks = new JLabel("");
            delegate.add(new JLabel("Marks"));
            delegate.add(labelMarks);
        }

        // Mark selection type
        {
            comboMarkSelection = new JComboBox<>();
            comboMarkSelection.addItem(new NameIntValue("any", MARKSELECTION_ANY));
            comboMarkSelection.addItem(new NameIntValue("all", MARKSELECTION_ALL));

            delegate.add(new JLabel("Mark Selection Condition"));
            delegate.add(comboMarkSelection);
        }

        // Minimum execution time
        {
            textFieldMinimumExecutionTime = new JTextField("0");

            delegate.add(new JLabel("Minimum Execution Time"));
            delegate.add(textFieldMinimumExecutionTime);
        }
    }

    private boolean isMarkSelectionAll() {
        NameIntValue selectedItem = (NameIntValue) comboMarkSelection.getSelectedItem();
        return selectedItem.getValue() == MARKSELECTION_ALL;
    }

    public void setMarksText(String text) {
        labelMarks.setText(text);
    }

    public MatchKernel createMatch(IndicesSelection markSelection) {

        NameIntValue selectedItem = (NameIntValue) comboKernelType.getSelectedItem();
        MatchKernel match = new MatchKernelAny(selectedItem.getValue());

        selectedItem = (NameIntValue) comboAccepted.getSelectedItem();
        if (selectedItem.getValue() != ACCEPTED_ANY) {
            // We add an additional condition
            MatchKernel matchAdd =
                    new MatchKernelAccptd(selectedItem.getValue() == ACCEPTED_ACCEPTED);
            match = new MatchKernelAnd(match, matchAdd);
        }

        selectedItem = (NameIntValue) comboProposed.getSelectedItem();
        if (selectedItem.getValue() != PROPOSED_ANY) {
            // We add an additional condition
            MatchKernel matchAdd =
                    new MatchKernelProp(selectedItem.getValue() == PROPOSED_PROPOSED);
            match = new MatchKernelAnd(match, matchAdd);
        }

        // Then we have some marks selected, and should do something
        if (!markSelection.isEmpty()) {
            MatchKernel matchAdd =
                    isMarkSelectionAll()
                            ? new MatchKernelAllChangedMarksFoundIn(markSelection)
                            : new MatchKernelAnyChangedMarksFoundIn(markSelection);
            match = new MatchKernelAnd(match, matchAdd);
        }

        // Minimum execution time
        if (!textFieldMinimumExecutionTime.getText().isEmpty()
                && !textFieldMinimumExecutionTime.getText().equals("0")) {

            long minimumExecutionTime = Long.parseLong(textFieldMinimumExecutionTime.getText());

            MatchKernel matchAdd =
                    new MatchKernelExecutionTimeGreaterThanEquals(minimumExecutionTime);
            match = new MatchKernelAnd(match, matchAdd);
        }

        return match;
    }

    public JPanel getPanel() {
        return delegate;
    }

    private static class NameIntValue {
        private String name;
        private int value;

        public NameIntValue(String name, int value) {
            super();
            this.name = name;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
