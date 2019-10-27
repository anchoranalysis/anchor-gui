package org.anchoranalysis.gui.frame.canvas;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.geometry.Point2i;

class MouseMotionListenerDragView extends MouseInputAdapter {

	private UpdateThread updateThread;
	private Point2i origPnt = null;
	
	private DisplayStackViewportZoomed displayStackViewport;
			
	public MouseMotionListenerDragView(ImageCanvas imageCanvas, DisplayStackViewportZoomed displayStackViewport,
			ErrorReporter errorReporter) {
		super();
		this.updateThread = new UpdateThread(imageCanvas,errorReporter);
		this.displayStackViewport = displayStackViewport;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
			
		if (origPnt!=null) {
			
			if (updateThread.isRunning()==false) {
				//System.out.println("Mouse dragging");
				doShift(e,true);
			} else {
				//System.out.println("rejected in thread");
			}
		} 
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (!e.isPopupTrigger() && (e.isControlDown()||e.isShiftDown())) {
			
			//System.out.println("capturing");
			
			origPnt = new Point2i();
			origPnt.setX( e.getX() );
			origPnt.setY( e.getY() );
		}
	}

	private void doShift( MouseEvent e, boolean enforceTolerance ) {
		int shiftX = origPnt.getX() - e.getX();
		int shiftY = origPnt.getY() - e.getY();
		
		// We construct a new mouse point with the shift
		
		//Point2i mousePoint = new Point2i( e.getX() - shiftX, e.getY() - shiftY );
		
		// We ignore unless they are enough to nudge us forward a bit
		// This prevents loads of small shifts being ignored as we drag
		if (enforceTolerance) {
			int mult = 4;
			double minNeeded = displayStackViewport.getZoomScale().getScale()*mult;
			if (Math.abs(shiftX)<minNeeded && Math.abs(shiftY)<minNeeded) {
				return;
			}
		}
		
		//System.out.printf("Do shift %d and %d\n", shiftX, shiftY );
		
		updateThread.setShift( new Point2i(shiftX,shiftY) );
		new Thread(updateThread).start();
					
		origPnt.setX( e.getX() );
		origPnt.setY( e.getY() );
		
	}
	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		
		
		if (origPnt!=null) {
		
			//System.out.println("Mouse released doing it");
			doShift(e,false);
			
			
			origPnt = null;	
		}
	}
}