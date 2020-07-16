/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.geometry.Point2i;

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
