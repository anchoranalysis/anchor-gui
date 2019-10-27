package org.anchoranalysis.gui.videostats.dropdown;

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


import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuListener;

// Allows us to do stuff to either a Menu or MenuPopup depending on which was picked
public class DualMenuWrapper {

	private JMenu menu = null;
	private JPopupMenu menuPopup = null;

	public DualMenuWrapper( JMenu menu ) {
		super();
		this.menu = menu;
	}
	
	// ROOT
	public DualMenuWrapper( JPopupMenu menu ) {
		super();
		this.menuPopup = menu;
	}
	
	
	public void add( JMenuItem item ) {
		
		if (menu!=null) {
			this.menu.add( item );
		}
		
		if (menuPopup!=null) {
			this.menuPopup.add( item );
		}
	}
	
	public void addSeparator() {
		
		if (menu!=null) {
			this.menu.addSeparator();
		}
		
		if (menuPopup!=null) {
			this.menuPopup.addSeparator();
		}
	}

	public void addMenuListener(MenuListener l) {
		
		if (menu!=null) {
			this.menu.addMenuListener(l);
		}
		
		// NO menu listener for menuPopUp
		if (menuPopup!=null) {
			assert false;
		}
	}
	
	public void addSeperator() {
		
		if (menu!=null) {
			this.menu.addSeparator();
		}
		
		if (menuPopup!=null) {
			this.menuPopup.addSeparator();
		}
	}
	
	public String getText() {
		
		if (menu!=null) {
			return menu.getText();
		}
		
		if (menuPopup!=null) {
			return menuPopup.getLabel();
		}
		
		assert false;
		return "";
	}
}
