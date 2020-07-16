/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.EventListenerList;

// We ignore any of these events if CONTROL or SHIFT is pressed, as we reserve
///  these for ourselves
class MouseListenerRel implements MouseListener {

    private MouseEventCreator eventCreator;
    private EventListenerList eventList;

    public MouseListenerRel(MouseEventCreator eventCreator, EventListenerList eventList) {
        super();
        this.eventCreator = eventCreator;
        this.eventList = eventList;
    }

    private MouseListener[] getListeners() {
        return eventList.getListeners(MouseListener.class);
    }

    private boolean isIgnored(MouseEvent me) {
        return me.isControlDown() || me.isShiftDown();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

        for (MouseListener el : getListeners()) {
            el.mouseClicked(eventCreator.mouseEventNew(arg0));
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

        if (isIgnored(arg0)) {
            return;
        }

        for (MouseListener el : getListeners()) {
            el.mouseEntered(eventCreator.mouseEventNew(arg0));
        }
    }

    @Override
    public void mouseExited(MouseEvent arg0) {

        if (isIgnored(arg0)) {
            return;
        }

        for (MouseListener el : getListeners()) {
            el.mouseExited(eventCreator.mouseEventNew(arg0));
        }
    }

    @Override
    public void mousePressed(MouseEvent arg0) {

        for (MouseListener el : getListeners()) {
            el.mousePressed(eventCreator.mouseEventNew(arg0));
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

        for (MouseListener el : getListeners()) {
            el.mouseReleased(eventCreator.mouseEventNew(arg0));
        }
    }
}
