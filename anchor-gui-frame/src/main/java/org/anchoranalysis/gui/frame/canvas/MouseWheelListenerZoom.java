/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.anchoranalysis.core.geometry.Point2i;

class MouseWheelListenerZoom implements MouseWheelListener {

    private ImageCanvas imageCanvas;

    public MouseWheelListenerZoom(ImageCanvas imageCanvas) {
        super();
        this.imageCanvas = imageCanvas;
    }

    private void changeZoom(int notches, Point2i mousePoint) {

        if (notches < 0) {
            imageCanvas.zoomIn(mousePoint);
        } else {
            imageCanvas.zoomOut(mousePoint);
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        int notches = e.getWheelRotation();

        if (!e.isPopupTrigger() && (e.isControlDown() || e.isShiftDown())) {

            // Point2i point = cnvrtCrnrPoint( e.getX(), e.getY() );
            Point2i point = new Point2i(e.getX(), e.getY());
            changeZoom(notches, point);
        }

        //			String newline = "\n";
        //			String message;
        //		       if (notches < 0) {
        //		           message = "Mouse wheel moved UP "
        //		                        + -notches + " notch(es)" + newline;
        //		       } else {
        //		           message = "Mouse wheel moved DOWN "
        //		                        + notches + " notch(es)" + newline;
        //		       }
        //
        //
        //		       if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
        //		           message += "    Scroll type: WHEEL_UNIT_SCROLL" + newline;
        //		           message += "    Scroll amount: " + e.getScrollAmount()
        //		                   + " unit increments per notch" + newline;
        //		           message += "    Units to scroll: " + e.getUnitsToScroll()
        //		                   + " unit increments" + newline;
        //
        //		       } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
        //		           message += "    Scroll type: WHEEL_BLOCK_SCROLL" + newline;
        //
        //		       }
        //		       System.out.print(message);

    }
}
