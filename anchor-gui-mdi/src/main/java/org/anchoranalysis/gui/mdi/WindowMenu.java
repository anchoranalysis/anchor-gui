/*-
 * #%L
 * anchor-gui-mdi
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
package org.anchoranalysis.gui.mdi;



import javax.swing.*;
import javax.swing.event.*;

import org.anchoranalysis.core.error.reporter.ErrorReporter;

import java.awt.event.*;
import java.beans.*;


public class WindowMenu extends JMenu {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7036338888517045966L;
	
	private MDIDesktopPane desktop;
    private JMenuItem cascade=new JMenuItem("Cascade", 'c');
    private JMenuItem tile=new JMenuItem("Tile", 't');
    private JMenuItem smartArrange=null;

    private ErrorReporter errorReporter;
    
    // smartArrange is an optional additional way of arranging the windows
    public WindowMenu(MDIDesktopPane desktop, final IArrangeFrames smartArrangeIn, ErrorReporter errorReporter) {
        this.desktop=desktop;
        this.errorReporter = errorReporter;
        setText("Window");
        setMnemonic('w');
        
        if (smartArrangeIn!=null) {
        	smartArrange=new JMenuItem("Smart Arrange", 's');
        	smartArrange.addActionListener( new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					smartArrangeIn.arrange();
				}
			} );
        }
        
        cascade.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.cascadeFrames();
            }
        });
        tile.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent ae) {
                WindowMenu.this.desktop.tileFrames();
            }
        });
        addMenuListener(new MenuListener() {
            @Override
			public void menuCanceled (MenuEvent e) {}

            @Override
			public void menuDeselected (MenuEvent e) {
                removeAll();
            }

            @Override
			public void menuSelected (MenuEvent e) {
                buildChildMenus();
            }
        });
    }

    /* Sets up the children menus depending on the current desktop state */
    private void buildChildMenus() {
        int i;
        ChildMenuItem menu;
        JInternalFrame[] array = desktop.getAllFrames();

        if (smartArrange!=null) {
        	add(smartArrange);
        }
        add(cascade);
        add(tile);
        if (array.length > 0) addSeparator();
        cascade.setEnabled(array.length > 0);
        tile.setEnabled(array.length > 0);

        for (i = 0; i < array.length; i++) {
            menu = new ChildMenuItem(array[i]);
            menu.setState(i == 0);
            menu.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
                    JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
                    frame.moveToFront();
                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException e) {
                        errorReporter.recordError(WindowMenu.class, e);
                    }
                }
            });
            menu.setIcon(array[i].getFrameIcon());
            add(menu);
        }
    }

    /* This JCheckBoxMenuItem descendant is used to track the child frame that corresponds
       to a give menu. */
    class ChildMenuItem extends JCheckBoxMenuItem {
        /**
		 * 
		 */
		private static final long serialVersionUID = 9162339434132160056L;
		private JInternalFrame frame;

        public ChildMenuItem(JInternalFrame frame) {
            super(frame.getTitle());
            this.frame=frame;
        }

        public JInternalFrame getFrame() {
            return frame;
        }
    }
}
