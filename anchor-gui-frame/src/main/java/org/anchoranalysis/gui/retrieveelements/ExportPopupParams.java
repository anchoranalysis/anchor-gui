/* (C)2020 */
package org.anchoranalysis.gui.retrieveelements;

import javax.swing.JFrame;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.io.generator.sequence.SequenceMemory;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class ExportPopupParams {
    private JFrame parentFrame;
    private BoundOutputManagerRouteErrors outputManager;
    private ErrorReporter errorReporter;
    private SequenceMemory sequenceMemory;

    public ExportPopupParams(ErrorReporter errorReporter) {
        super();
        this.errorReporter = errorReporter;
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    public BoundOutputManagerRouteErrors getOutputManager() {
        return outputManager;
    }

    public void setOutputManager(BoundOutputManagerRouteErrors outputManager) {
        this.outputManager = outputManager;
    }

    public SequenceMemory getSequenceMemory() {
        return sequenceMemory;
    }

    public void setSequenceMemory(SequenceMemory sequenceMemory) {
        this.sequenceMemory = sequenceMemory;
    }

    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }
}
