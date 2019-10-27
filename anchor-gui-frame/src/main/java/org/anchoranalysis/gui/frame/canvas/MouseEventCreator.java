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


import java.awt.Component;
import java.awt.event.MouseEvent;

import org.anchoranalysis.core.geometry.Point2i;

class MouseEventCreator {
	
	private ImageCanvasSwing imageCanvas;
	private DisplayStackViewportZoomed displayStackViewport;
			
	public MouseEventCreator(ImageCanvasSwing imageCanvas,
			DisplayStackViewportZoomed displayStackViewport) {
		super();
		this.imageCanvas = imageCanvas;
		this.displayStackViewport = displayStackViewport;
	}

	private Point2i cnvrtCrnrPoint( int x, int y) {
		Point2i crnrPnt = imageCanvas.getImageCrnrPoint();
		
		int xNew = displayStackViewport.cnvrtCanvasXToImage( x - crnrPnt.getX() ); 
		int yNew = displayStackViewport.cnvrtCanvasYToImage( y - crnrPnt.getY() );
		return new Point2i(xNew,yNew);
	}

	public MouseEvent mouseEventNew( MouseEvent evOld ) {
		
		Point2i pntNew = cnvrtCrnrPoint( evOld.getX(), evOld.getY() );
		
		MouseEvent evNew = new MouseEvent(
			((Component) evOld.getSource()),
			evOld.getID(),
			evOld.getWhen(),
			evOld.getModifiersEx(),
			pntNew.getX(),
			pntNew.getY(),
			evOld.getClickCount(),
			evOld.isPopupTrigger()
		);
		return evNew;
	}
}