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

package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.anchoranalysis.annotation.wholeimage.WholeImageLabelAnnotation;
import org.anchoranalysis.gui.annotation.bean.label.AnnotationLabel;
import org.anchoranalysis.gui.annotation.bean.label.GroupedAnnotationLabels;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelWithLabel;

class ChooseLabel extends PanelWithLabel {

    private GroupedAnnotationLabels groupedLabels;
    private Function<AnnotationLabel, Action> createAction;
    private Supplier<Optional<WholeImageLabelAnnotation>> readCurrentAnnotation;

    public ChooseLabel(
            Supplier<Optional<WholeImageLabelAnnotation>> readCurrentAnnotation,
            GroupedAnnotationLabels labelsIn,
            Function<AnnotationLabel, Action> createAction) {
        assert (labelsIn != null);
        this.groupedLabels = labelsIn;
        this.createAction = createAction;
        this.readCurrentAnnotation = readCurrentAnnotation;
        super.init("Please select an appropriate label for the image");
    }

    @Override
    protected JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // This will return NULL if no label exists
        Optional<WholeImageLabelAnnotation> existingLabel = readCurrentAnnotation.get();

        int row = 0;
        for (String group : groupedLabels.keySet()) {

            JPanel groupPanel = createGroupPanel(groupedLabels.get(group), existingLabel);
            addComponent(panel, groupPanel, row++);
        }
        return panel;
    }

    private JPanel createGroupPanel(
            Collection<AnnotationLabel> labels, Optional<WholeImageLabelAnnotation> existingLabel) {
        JPanel groupPanel = new JPanel();
        groupPanel.setLayout(new FlowLayout());

        for (AnnotationLabel lab : labels) {
            addButton(groupPanel, lab, existingLabel);
        }

        return groupPanel;
    }

    private void addButton(
            JPanel panel,
            AnnotationLabel label,
            Optional<WholeImageLabelAnnotation> existingLabel) {
        JButton button = new JButton(createAction.apply(label));

        // We always color the existing label to be black, otherwise we use the color associated
        // with the label
        if (existingLabel.isPresent()
                && label.getUniqueLabel().equals(existingLabel.get().getLabel())) {
            ColorUtilities.addColor(button, Color.BLACK);
        } else {
            ColorUtilities.maybeAddColor(button, label.getColor());
        }

        panel.add(button);
    }

    private static void addComponent(JPanel panel, JComponent componentToAdd, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = gridy;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        panel.add(componentToAdd, c);
    }
}
