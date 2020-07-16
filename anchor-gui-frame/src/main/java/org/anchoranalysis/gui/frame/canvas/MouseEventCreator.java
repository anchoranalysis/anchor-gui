/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.awt.Component;
import java.awt.event.MouseEvent;
import org.anchoranalysis.core.geometry.Point2i;

class MouseEventCreator {

    private ImageCanvasSwing imageCanvas;
    private DisplayStackViewportZoomed displayStackViewport;

    public MouseEventCreator(
            ImageCanvasSwing imageCanvas, DisplayStackViewportZoomed displayStackViewport) {
        super();
        this.imageCanvas = imageCanvas;
        this.displayStackViewport = displayStackViewport;
    }

    private Point2i cnvrtCrnrPoint(int x, int y) {
        Point2i crnrPoint = imageCanvas.getImageCrnrPoint();

        int xNew = displayStackViewport.cnvrtCanvasXToImage(x - crnrPoint.getX());
        int yNew = displayStackViewport.cnvrtCanvasYToImage(y - crnrPoint.getY());
        return new Point2i(xNew, yNew);
    }

    public MouseEvent mouseEventNew(MouseEvent evOld) {

        Point2i pointNew = cnvrtCrnrPoint(evOld.getX(), evOld.getY());

        MouseEvent evNew =
                new MouseEvent(
                        ((Component) evOld.getSource()),
                        evOld.getID(),
                        evOld.getWhen(),
                        evOld.getModifiers(),
                        pointNew.getX(),
                        pointNew.getY(),
                        evOld.getClickCount(),
                        evOld.isPopupTrigger());
        return evNew;
    }
}
