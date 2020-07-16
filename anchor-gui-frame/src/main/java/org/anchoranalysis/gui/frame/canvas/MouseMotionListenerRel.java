/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.event.EventListenerList;

class MouseMotionListenerRel implements MouseMotionListener {

    private MouseEventCreator eventCreator;
    private EventListenerList eventList;

    public MouseMotionListenerRel(MouseEventCreator eventCreator, EventListenerList eventList) {
        super();
        this.eventCreator = eventCreator;
        this.eventList = eventList;
    }

    private MouseMotionListener[] getListeners() {
        return eventList.getListeners(MouseMotionListener.class);
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        for (MouseMotionListener el : getListeners()) {
            el.mouseDragged(eventCreator.mouseEventNew(arg0));
        }
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {

        for (MouseMotionListener el : getListeners()) {
            el.mouseMoved(eventCreator.mouseEventNew(arg0));
        }
    }
}
