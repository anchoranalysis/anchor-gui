/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class MouseWheelListenerSlices implements MouseWheelListener {

    private SliceIndexSlider slider;

    public MouseWheelListenerSlices(SliceIndexSlider slider) {
        super();
        this.slider = slider;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.isPopupTrigger()) {
            return;
        }

        int notches = e.getWheelRotation();

        if (notches < 0) {
            slider.decrementStack();
        } else {
            slider.incrementStack();
        }
    }
}
