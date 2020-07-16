/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.annotation.WrapAction;
import org.anchoranalysis.gui.frame.details.canvas.ControllerKeyboard;
import org.anchoranalysis.gui.frame.details.canvas.ControllerMouse;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelMark;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelTool;

public class AddConfirmRotate {

    public static void apply(
            PanelTool panelTool,
            PanelMark panelMark,
            ISliderState sliderState,
            ControllerKeyboard controllerKeyboard,
            ControllerMouse controllerMouse) {

        AbstractAction opConfirm = new WrapAction(e -> confirm(panelTool, panelMark));

        bindKeyboard(panelTool, controllerKeyboard.getActionMap(), opConfirm);

        bindMiddleMouseButton(panelTool, panelMark, sliderState, controllerMouse);

        panelMark.addActionListenerConfirm(opConfirm);
    }

    private static void bindKeyboard(
            PanelTool panelTool, ActionMap actionMap, AbstractAction opConfirm) {
        actionMap.put("confirmMark", opConfirm);

        actionMap.put("rotateToolRight", new WrapAction((e) -> panelTool.switchRotateRight()));

        actionMap.put("rotateToolLeft", new WrapAction((e) -> panelTool.switchRotateLeft()));
    }

    private static void bindMiddleMouseButton(
            PanelTool panelTool,
            PanelMark panelMark,
            ISliderState sliderState,
            ControllerMouse controllerMouse) {
        // We bind the middle mouse button to be the same as "confirmMark"
        controllerMouse.addMouseListener(
                new MouseClickAdapter(
                        () -> confirm(panelTool, panelMark), // Middle-Mouse

                        // Left-Mouse
                        e -> {
                            Point3d point =
                                    new Point3d(e.getX(), e.getY(), sliderState.getSliceNum());
                            panelTool.getTool().leftMouseClickedAtPoint(point);
                        }),
                false);
    }

    private static void confirm(PanelTool panelTool, PanelMark panelMark) {
        panelTool.getTool().confirm(panelMark.isAccepted());
    }
}
