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


import javax.swing.JScrollBar;
import javax.swing.event.ChangeListener;

import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.image.extent.Extent;

// Two scroll bars mapping a 2d extnt
class ExtntScrollBars {
	
	// Our scrollbars
	private JScrollBar scrollHor;
	private JScrollBar scrollVer;
	
	// Whether to trigger a change event when the user is *adjusting* the scroll bar
	private boolean includeAdjusting = true;
	
	
	private LocalChangeListener localChangeListener;
	
	public ExtntScrollBars() {
		this.scrollHor = new JScrollBar( JScrollBar.HORIZONTAL );
		this.scrollVer = new JScrollBar( JScrollBar.VERTICAL );
		this.scrollHor.setVisible(false);
		this.scrollVer.setVisible(false);
		
		localChangeListener = new LocalChangeListener(includeAdjusting);
	}
	
	
	public void addChangeListener( ChangeListener cl ) {
		localChangeListener.addChangeListener(cl);

		this.scrollHor.getModel().addChangeListener( localChangeListener );
		this.scrollVer.getModel().addChangeListener( localChangeListener );
	}

	public JScrollBar getScrollVer() {
		return scrollVer;
	}

	public JScrollBar getScrollHor() {
		return scrollHor;
	}

	public void setVisibleAmount( Extent e ) {
		scrollHor.setVisibleAmount( e.getX() );
		scrollVer.setVisibleAmount( e.getY() );
	}
	
	public void setMinMax( Extent e, boolean alwaysAllowChangeInVisbility ) {
		localChangeListener.disableEvents();
		scrollHor.setMinimum(0);
		scrollHor.setMaximum( e.getX() - 1 );
		
		scrollVer.setMinimum(0);
		scrollVer.setMaximum( e.getY() -1 );
		
		if (scrollHor.isVisible()==false || alwaysAllowChangeInVisbility) {
			scrollHor.setVisible( (scrollHor.getVisibleAmount()>0) && (scrollHor.getVisibleAmount() < (scrollHor.getMaximum()-scrollHor.getMinimum()) ));
		}
		if (scrollVer.isVisible()==false || alwaysAllowChangeInVisbility) {
			scrollVer.setVisible( (scrollVer.getVisibleAmount()>0) && (scrollVer.getVisibleAmount() < (scrollVer.getMaximum()-scrollVer.getMinimum()) ));
		}
		localChangeListener.enableEvents();
		//System.out.printf("Testing if visible hor=%s ver=%s (vis=%d,%d) (extnt=%d,%d)\n", scrollHor.isVisible()?"true":"false", scrollVer.isVisible()?"true":"false", scrollHor.getVisibleAmount(), scrollVer.getVisibleAmount(), e.getX(), e.getY() );
	}
	
	// Considers the width of the vertical scrollbar, and the height of the horizontal scrollbar and returns the maximum of the two
	public int maxSizeOfScrollbar() {
		return Math.max( scrollHor.getHeight(), scrollVer.getWidth() );
	}
	
	public Point2i value() {
		return new Point2i( scrollHor.getValue(), scrollVer.getValue() );
	}
	
	public void setValue( Point2i val ) {
		localChangeListener.disableEvents();
		scrollHor.setValue(val.getX());
		scrollVer.setValue(val.getY());
		localChangeListener.enableEvents();
	}
	
	public int getPreferredHeight() {
//		double height = 0;
//		
////		if (scrollHor.isVisible()) {
////			height += scrollHor.getPreferredSize().getHeight();
////		}
//		
//		if (scrollVer.isVisible()) {
//			height += scrollVer.getPreferredSize().getHeight();
//		}
//		
//		return (int) (height);
		return 0;
	}
	
	public int getPreferredWidth() {
		
//		double width = 0;
//		
//		if (scrollHor.isVisible()) {
//			width += scrollHor.getPreferredSize().getWidth();
//		}
//		
////		if (scrollVer.isVisible()) {
////			width += scrollVer.getPreferredSize().getWidth();
////		}
//		
//		return (int) (width);
		
		return 0;
	}
}
