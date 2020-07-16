package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
		ControllerMouse controllerMouse
	) {
		
		AbstractAction opConfirm = new WrapAction( e-> confirm(panelTool, panelMark) );
		
		bindKeyboard(panelTool, controllerKeyboard.getActionMap(), opConfirm);
		
		bindMiddleMouseButton(panelTool, panelMark, sliderState, controllerMouse);
		
		panelMark.addActionListenerConfirm(opConfirm);
	}
	
	private static void bindKeyboard(
		PanelTool panelTool,
		ActionMap actionMap,
		AbstractAction opConfirm
	) {
		actionMap.put("confirmMark", opConfirm );
		
		actionMap.put(
			"rotateToolRight",
			new WrapAction(
				(e) -> panelTool.switchRotateRight()
			)
		);
		
		actionMap.put(
			"rotateToolLeft",
			new WrapAction(
				(e) -> panelTool.switchRotateLeft()
			)
		);
	}
	

	private static void bindMiddleMouseButton(
		PanelTool panelTool,
		PanelMark panelMark,
		ISliderState sliderState,
		ControllerMouse controllerMouse
	) {
		// We bind the middle mouse button to be the same as "confirmMark"
		controllerMouse.addMouseListener(
			new MouseClickAdapter(
				()->confirm(panelTool, panelMark),	// Middle-Mouse
				
				// Left-Mouse
				e -> {
					Point3d point = new Point3d(
						e.getX(),
						e.getY(),
						sliderState.getSliceNum()
					);
					panelTool.getTool().leftMouseClickedAtPoint( point );
				}
			),
			false
		);		
	}
		
	private static void confirm( PanelTool panelTool, PanelMark panelMark ) {
		panelTool.getTool().confirm(
			panelMark.isAccepted()
		);	
	}
}
