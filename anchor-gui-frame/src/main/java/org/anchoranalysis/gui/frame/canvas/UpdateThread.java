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

package org.anchoranalysis.gui.frame.canvas;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.spatial.point.Point2i;

class UpdateThread implements Runnable {

    private boolean running = false;
    private Point2i shift;

    private ImageCanvas imageCanvas;
    private ErrorReporter errorReporter;

    public UpdateThread(ImageCanvas imageCanvas, ErrorReporter errorReporter) {
        super();
        this.imageCanvas = imageCanvas;
        this.errorReporter = errorReporter;
    }

    @Override
    public void run() {
        try {
            imageCanvas.shiftViewport(shift);
        } catch (OperationFailedException e) {
            errorReporter.recordError(ImageCanvas.class, e);
        }
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void setShift(Point2i shift) {
        this.running = true;
        this.shift = shift;
    }
}
