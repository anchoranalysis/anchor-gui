/* (C)2020 */
package org.anchoranalysis.gui.frame.details.canvas;

public class ControllerAction {

    private ControllerOrder controllerOrder;
    private ControllerFrame controllerFrame;
    private ControllerMouse controllerMouse;
    private ControllerKeyboard controllerKeyboard;

    public ControllerAction(
            ControllerOrder controllerOrder,
            ControllerFrame controllerFrame,
            ControllerMouse controllerMouse,
            ControllerKeyboard controllerKeyboard) {
        super();
        this.controllerOrder = controllerOrder;
        this.controllerFrame = controllerFrame;
        this.controllerMouse = controllerMouse;
        this.controllerKeyboard = controllerKeyboard;
    }

    public ControllerOrder order() {
        return controllerOrder;
    }

    public ControllerFrame frame() {
        return controllerFrame;
    }

    public ControllerMouse mouse() {
        return controllerMouse;
    }

    public ControllerKeyboard keyboard() {
        return controllerKeyboard;
    }
}
