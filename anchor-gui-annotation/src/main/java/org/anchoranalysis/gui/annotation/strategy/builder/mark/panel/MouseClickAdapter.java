/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

class MouseClickAdapter extends MouseAdapter {

	private Runnable middleMouse;
	private Consumer<MouseEvent> leftMouse;
	
	public MouseClickAdapter(
		Runnable middleMouse,
		Consumer<MouseEvent> leftMouse
	) {
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
