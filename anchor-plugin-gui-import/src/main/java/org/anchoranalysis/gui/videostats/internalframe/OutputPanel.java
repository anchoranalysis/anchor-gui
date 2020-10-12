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
import java.awt.event.ActionEvent;
import java.util.Optional;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.image.io.bean.object.draw.Outline;
import org.anchoranalysis.io.generator.sequence.OutputSequenceIncrementing;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.mpp.io.marks.ColoredMarksWithDisplayStack;
import org.anchoranalysis.mpp.io.marks.MarksWithDisplayStack;
import org.anchoranalysis.mpp.io.marks.generator.MarksGenerator;
import org.anchoranalysis.mpp.mark.IDGetterMarkID;
import org.anchoranalysis.overlay.id.IDGetterOverlayID;

public class OutputPanel {

    private JPanel panel;

    private InputOutputContext context;

    private OutputSequenceIncrementing<ColoredMarksWithDisplayStack> outputSequence;

    private ColorIndex colorIndex;

    private class StartAction extends AbstractAction {

        private static final long serialVersionUID = 6082901454998007224L;

        public StartAction() {
            super("Start Sequence");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            MarksGenerator generator = new MarksGenerator(new Outline(2), new IDGetterOverlayID());

            OutputPatternIntegerSuffix directory = new OutputPatternIntegerSuffix("markEvaluator", 6, false, Optional.of(new ManifestDescription("raster", "markEvaluator")));

            OutputterChecked outputter = context.getOutputter().getChecked();
            
            try {
                outputSequence = new OutputSequenceFactory<>(generator, outputter).incrementingByOne(directory);
            } catch (OutputWriteFailedException e) {
                context.getErrorReporter().recordError(OutputPanel.class, e);
                outputSequence = null;
            }
        }
    }

    private class EndAction extends AbstractAction {

        /** */
        private static final long serialVersionUID = -1812205932710878624L;

        public EndAction() {
            super("End Sequence");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            outputSequence = null;
        }
    }

    public OutputPanel() {

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        createAndButton(new StartAction(), panel);
        createAndButton(new EndAction(), panel);
    }

    public void init(ColorIndex colorIndex, InputOutputContext context) {
        this.colorIndex = colorIndex;
        this.context = context;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void output(MarksWithDisplayStack cws) {
        if (outputSequence != null) {

            ColoredMarksWithDisplayStack colored =
                    new ColoredMarksWithDisplayStack(cws, colorIndex, new IDGetterMarkID());
            try {
                outputSequence.add(colored);
            } catch (OutputWriteFailedException e) {
                context.getErrorReporter().recordError(OutputPanel.class, e);
                outputSequence = null;
            }
        }
    }
        
    private static void createAndButton(Action action, JPanel panel) {
        panel.add(new JButton(action)); 
    }
}
