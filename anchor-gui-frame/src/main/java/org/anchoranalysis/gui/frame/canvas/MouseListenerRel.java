/*-
 * #%L
 * anchor-gui-frame
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
package org.anchoranalysis.gui.frame.canvas;



import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.EventListenerList;

// We ignore any of these events if CONTROL or SHIFT is pressed, as we reserve
///  these for ourselves
class MouseListenerRel implements MouseListener {

	private MouseEventCreator eventCreator;
	private EventListenerList eventList;
	
	public MouseListenerRel(MouseEventCreator eventCreator,
			EventListenerList eventList) {
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
		
		for( MouseListener el : getListeners() ) {
			el.mouseClicked( eventCreator.mouseEventNew(arg0) );
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		if (isIgnored(arg0)) {
			return;
		}
		
		for( MouseListener el : getListeners() ) {
			el.mouseEntered( eventCreator.mouseEventNew(arg0) );
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
		if (isIgnored(arg0)) {
			return;
		}
		
		for( MouseListener el : getListeners() ) {
			el.mouseExited( eventCreator.mouseEventNew(arg0) );
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
		for( MouseListener el : getListeners() ) {
			el.mousePressed( eventCreator.mouseEventNew(arg0) );
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
		for( MouseListener el : getListeners() ) {
			el.mouseReleased( eventCreator.mouseEventNew(arg0) );
		}
	}
}
