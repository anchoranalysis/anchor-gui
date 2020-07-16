/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.anchoranalysis.anchor.mpp.mark.IDGetterMarkID;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.bean.object.writer.Outline;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceIncrementalRerouteErrors;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceIncrementalWriter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.io.cfg.CfgWithDisplayStack;
import org.anchoranalysis.mpp.io.cfg.ColoredCfgWithDisplayStack;
import org.anchoranalysis.mpp.io.cfg.generator.CfgGenerator;

public class OutputPanel {

    private JPanel panel;

    private BoundOutputManagerRouteErrors outputManager;

    private GeneratorSequenceIncrementalRerouteErrors<ColoredCfgWithDisplayStack> sequenceWriter;

    private ColorIndex colorIndex;

    private final ErrorReporter errorReporter;

    private class StartAction extends AbstractAction {

        private static final long serialVersionUID = 6082901454998007224L;

        public StartAction() {
            super("Start Sequence");
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            CfgGenerator generator = new CfgGenerator(new Outline(2), new IDGetterOverlayID());

            IndexableOutputNameStyle outputNameStyle =
                    new IntegerSuffixOutputNameStyle("markEvaluator", 6);

            sequenceWriter =
                    new GeneratorSequenceIncrementalRerouteErrors<>(
                            new GeneratorSequenceIncrementalWriter<>(
                                    outputManager.getDelegate(),
                                    outputNameStyle.getOutputName(),
                                    outputNameStyle,
                                    generator,
                                    new ManifestDescription("raster", "markEvaluator"),
                                    0,
                                    true),
                            errorReporter);

            sequenceWriter.start();
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
            if (sequenceWriter != null) {
                sequenceWriter.end();
                sequenceWriter = null;
            }
        }
    }

    public OutputPanel(ErrorReporter errorReporter) {

        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        {
            JButton button = new JButton(new StartAction());
            panel.add(button);
        }
        {
            JButton button = new JButton(new EndAction());
            panel.add(button);
        }

        this.errorReporter = errorReporter;
    }

    public void init(ColorIndex colorIndex, BoundOutputManagerRouteErrors outputManager) {
        this.colorIndex = colorIndex;
        this.outputManager = outputManager;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void output(CfgWithDisplayStack cws) {
        if (sequenceWriter != null) {

            ColoredCfgWithDisplayStack colored =
                    new ColoredCfgWithDisplayStack(cws, colorIndex, new IDGetterMarkID());
            sequenceWriter.add(colored);
        }
    }
}
