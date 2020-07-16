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


import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.anchoranalysis.core.geometry.Point2i;

class MouseWheelListenerZoom implements MouseWheelListener {

		private ImageCanvas imageCanvas;
				
		public MouseWheelListenerZoom(ImageCanvas imageCanvas) {
			super();
			this.imageCanvas = imageCanvas;
		}

		private void changeZoom( int notches, Point2i mousePoint ) {
				
			if (notches < 0) {
				imageCanvas.zoomIn(mousePoint);
			} else {
				imageCanvas.zoomOut(mousePoint);
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			
			int notches = e.getWheelRotation();
			
			if (!e.isPopupTrigger() && (e.isControlDown()||e.isShiftDown())) {
				
				//Point2i point = cnvrtCrnrPoint( e.getX(), e.getY() );
				Point2i point = new Point2i( e.getX(), e.getY() );
				changeZoom( notches, point );
			}
		       
		       
			
//			String newline = "\n";
//			String message; 
//		       if (notches < 0) {
//		           message = "Mouse wheel moved UP "
//		                        + -notches + " notch(es)" + newline;
//		       } else {
//		           message = "Mouse wheel moved DOWN "
//		                        + notches + " notch(es)" + newline;
//		       }
//		       
//		       
//		       if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
//		           message += "    Scroll type: WHEEL_UNIT_SCROLL" + newline;
//		           message += "    Scroll amount: " + e.getScrollAmount()
//		                   + " unit increments per notch" + newline;
//		           message += "    Units to scroll: " + e.getUnitsToScroll()
//		                   + " unit increments" + newline;
//		         
//		       } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
//		           message += "    Scroll type: WHEEL_BLOCK_SCROLL" + newline;
//		         
//		       }
//		       System.out.print(message);
			
		}
		
	}