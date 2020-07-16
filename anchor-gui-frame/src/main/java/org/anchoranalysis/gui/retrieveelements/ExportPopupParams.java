/*-
 * #%L
 * anchor-gui-frame
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
