/* (C)2020 */
package org.anchoranalysis.gui.frame.canvas;

import java.util.ArrayList;
import java.util.List;
import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class LocalChangeListener implements ChangeListener {

    private boolean includeAdjusting = true;
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    private boolean eventsAllowed = true;

    public LocalChangeListener(boolean includeAdjusting) {
        super();
        this.includeAdjusting = includeAdjusting;
    }

    public synchronized void enableEvents() {
        eventsAllowed = true;
    }

    public synchronized void disableEvents() {
        eventsAllowed = false;
    }

    @Override
    public synchronized void stateChanged(ChangeEvent changeEvent) {

        Object source = changeEvent.getSource();
        if (source instanceof BoundedRangeModel) {
            BoundedRangeModel aModel = (BoundedRangeModel) source;
            // System.out.println("Something changed: " + source);
            if (eventsAllowed) {

                if (includeAdjusting || !aModel.getValueIsAdjusting()) {
                    for (ChangeListener cl : listeners) {
                        cl.stateChanged(changeEvent);
                    }
                }
            }
        }
    }

    public void addChangeListener(ChangeListener cl) {
        listeners.add(cl);
    }
}
