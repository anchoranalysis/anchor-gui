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
import java.awt.event.MouseMotionListener;

import javax.swing.event.EventListenerList;

class MouseMotionListenerRel implements MouseMotionListener {

	private MouseEventCreator eventCreator;
	private EventListenerList eventList;
			
	public MouseMotionListenerRel(MouseEventCreator eventCreator,
			EventListenerList eventList) {
		super();
		this.eventCreator = eventCreator;
		this.eventList = eventList;
	}

	private MouseMotionListener[] getListeners() {
		return eventList.getListeners(MouseMotionListener.class);
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		for( MouseMotionListener el : getListeners() ) {
			el.mouseDragged( eventCreator.mouseEventNew(arg0) );
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
		for( MouseMotionListener el : getListeners() ) {
			el.mouseMoved( eventCreator.mouseEventNew(arg0) );
		}
		
		
	}
	
}
