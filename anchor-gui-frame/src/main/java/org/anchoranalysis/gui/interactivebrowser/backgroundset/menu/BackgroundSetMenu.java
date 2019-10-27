package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

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


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.IImageStackCntrFromName;

// A menu which allows changing of the background
class BackgroundSetMenu {

	private JMenu menu;
	
	public static class BackgroundChangeAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7238970062538672779L;

		private String backgroundSetName;
		private IImageStackCntrFromName stackCntrGetter;
		private IBackgroundSetter backgroundSetter;
		private ErrorReporter errorReporter;
		
		public BackgroundChangeAction( String backgroundSetName, IImageStackCntrFromName stackCntrGetter, IBackgroundSetter backgroundSetter, ErrorReporter errorReporter ) {
			super(backgroundSetName);
			assert( backgroundSetter!=null );
			this.backgroundSetName = backgroundSetName;
			this.stackCntrGetter = stackCntrGetter;
			this.backgroundSetter = backgroundSetter;
			this.errorReporter = errorReporter;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			try {
				backgroundSetter.setImageStackCntr( stackCntrGetter.imageStackCntrFromName(backgroundSetName) );
			} catch (GetOperationFailedException e) {
				errorReporter.recordError(BackgroundSetMenu.class, e);
				JOptionPane.showMessageDialog(null, String.format("Background set '%s' failed",backgroundSetName));
			} catch (SetOperationFailedException e) {
				errorReporter.recordError(BackgroundSetMenu.class, e);
				JOptionPane.showMessageDialog(null, String.format("Background set '%s' failed",backgroundSetName));
			}
		}
		
	}
	
	
	private ShowMenuListener showMenuListener;
	
	private static class ShowMenuListener implements MenuListener {
		
		private IGetNames nameGetter;
		private IImageStackCntrFromName stackCntrFromName;
		private IBackgroundSetter backgroundSetter;
		private JMenu menu;
		private ErrorReporter errorReporter;
		
		public ShowMenuListener(JMenu menu, IBackgroundSetter backgroundSetter, IGetNames nameGetter, IImageStackCntrFromName stackCntrFromName, ErrorReporter errorReporter ) {
			this.menu = menu;
			this.nameGetter = nameGetter;
			this.backgroundSetter = backgroundSetter;
			this.stackCntrFromName = stackCntrFromName;
			this.errorReporter = errorReporter;
		}
		
		private void addItems() {
			
			for( String name : nameGetter.names()) {
				menu.add( new BackgroundChangeAction(name, stackCntrFromName, backgroundSetter, errorReporter) );
			}		
		}

		public void setNameGetter(IGetNames nameGetter) {
			this.nameGetter = nameGetter;
		}

		public void setStackCntrFromName(IImageStackCntrFromName stackCntrFromName) {
			this.stackCntrFromName = stackCntrFromName;
		}

		@Override
		public void menuCanceled(MenuEvent arg0) {
		}

		@Override
		public void menuDeselected(MenuEvent arg0) {
		}

		@Override
		public void menuSelected(MenuEvent arg0) {
			menu.removeAll();
			addItems();
		}
	}
	
	
	public BackgroundSetMenu( final IGetNames nameGetter, IImageStackCntrFromName stackCntrFromName, IBackgroundSetter backgroundSetter, ErrorReporter errorReporter ) {
		assert( backgroundSetter!=null );
		menu = new JMenu("Background");
		showMenuListener = new ShowMenuListener(menu,backgroundSetter,nameGetter,stackCntrFromName, errorReporter );
		menu.addMenuListener( showMenuListener );
	}


	public JMenu getMenu() {
		return menu;
	}
	
	public void update( IGetNames nameGetter, IImageStackCntrFromName stackCntrFromName ) {
		showMenuListener.setNameGetter(nameGetter);
		showMenuListener.setStackCntrFromName(stackCntrFromName);
	}
	
}
