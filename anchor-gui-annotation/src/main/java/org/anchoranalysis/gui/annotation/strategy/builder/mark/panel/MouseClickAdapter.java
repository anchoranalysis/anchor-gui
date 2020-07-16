/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

class MouseClickAdapter extends MouseAdapter {

    private Runnable middleMouse;
    private Consumer<MouseEvent> leftMouse;

    public MouseClickAdapter(Runnable middleMouse, Consumer<MouseEvent> leftMouse) {
        super();
        this.middleMouse = middleMouse;
        this.leftMouse = leftMouse;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }

        // Middle mouse button
        if (SwingUtilities.isMiddleMouseButton(e)) {
            middleMouse.run();
            return;
        }

        // Left mouse button
        if (SwingUtilities.isLeftMouseButton(e)) {

            if (e.isControlDown() || e.isShiftDown() || e.isMetaDown()) {
                return;
            }

            leftMouse.accept(e);
            return;
        }
    }
}
