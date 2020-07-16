/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.geometry.Point2i;

class MouseMotionListenerDragView extends MouseInputAdapter {

    private UpdateThread updateThread;
    private Point2i origPoint = null;

    private DisplayStackViewportZoomed displayStackViewport;

    public MouseMotionListenerDragView(
            ImageCanvas imageCanvas,
            DisplayStackViewportZoomed displayStackViewport,
            ErrorReporter errorReporter) {
        super();
        this.updateThread = new UpdateThread(imageCanvas, errorReporter);
        this.displayStackViewport = displayStackViewport;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        if (origPoint != null) {

            if (updateThread.isRunning() == false) {
                // System.out.println("Mouse dragging");
                doShift(e, true);
            } else {
                // System.out.println("rejected in thread");
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        if (!e.isPopupTrigger() && (e.isControlDown() || e.isShiftDown())) {

            // System.out.println("capturing");

            origPoint = new Point2i();
            origPoint.setX(e.getX());
            origPoint.setY(e.getY());
        }
    }

    private void doShift(MouseEvent e, boolean enforceTolerance) {
        int shiftX = origPoint.getX() - e.getX();
        int shiftY = origPoint.getY() - e.getY();

        // We construct a new mouse point with the shift

        // Point2i mousePoint = new Point2i( e.getX() - shiftX, e.getY() - shiftY );

        // We ignore unless they are enough to nudge us forward a bit
        // This prevents loads of small shifts being ignored as we drag
        if (enforceTolerance) {
            int mult = 4;
            double minNeeded = displayStackViewport.getZoomScale().getScale() * mult;
            if (Math.abs(shiftX) < minNeeded && Math.abs(shiftY) < minNeeded) {
                return;
            }
        }

        // System.out.printf("Do shift %d and %d\n", shiftX, shiftY );

        updateThread.setShift(new Point2i(shiftX, shiftY));
        new Thread(updateThread).start();

        origPoint.setX(e.getX());
        origPoint.setY(e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (origPoint != null) {

            // System.out.println("Mouse released doing it");
            doShift(e, false);

            origPoint = null;
        }
    }
}
