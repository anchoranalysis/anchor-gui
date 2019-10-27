package org.anchoranalysis.gui.toolbar;

import java.awt.Component;

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


import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.anchoranalysis.gui.IconFactory;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.mdi.MDIDesktopPane;
import org.anchoranalysis.gui.mdi.PartitionedFrameList;
import org.anchoranalysis.gui.mdi.action.CascadeAction;
import org.anchoranalysis.gui.mdi.action.SmartArrangeAction;
import org.anchoranalysis.gui.mdi.action.TileAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowBoundingBoxAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowInsideAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowMidpointAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowOrientationLineAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowShellAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowSolidAction;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.ShowThickBorderAction;
import org.anchoranalysis.gui.videostats.frame.IAddableToolbar;

public class VideoStatsToolbar implements IAddableToolbar {


	
	/*private JButton createButton( String label, String toolTip ) {
		JButton button = new JButton( label );
		button.setToolTipText( toolTip );
		button.addActionListener( this.actionListener );
		return button;
	}*/
	
	private JToolBar delegate = new JToolBar();
	
	public void addButtonFromActionList( List<Action> actionList ) {
		for (Action action : actionList) {
			addButtonFromAction(action);
		}
	}
	
	private void addButtonFromAction( Action action ) {
		JButton button = new JButton(action);
		delegate.add(button);
	}
	
	public void addToggleButtonFromActionList( List<Action> actionList ) {
		for (Action action : actionList) {
			JToggleButton button = new JToggleButton(action);
			delegate.add(button);
		}
	}
	
	public void addWindowButtons( MDIDesktopPane desktopPane, PartitionedFrameList partitionedFrames) {
		
		IconFactory rf = new IconFactory();
		ArrayList<Action> actionListWindows = new ArrayList<>();
		actionListWindows.add( new SmartArrangeAction( desktopPane, partitionedFrames, rf.icon("/toolbarIcon/smart_arrange.png") ) );
		actionListWindows.add( new TileAction( desktopPane, rf.icon("/toolbarIcon/tile.png") ) );
		actionListWindows.add( new CascadeAction( desktopPane, rf.icon("/toolbarIcon/cascade.png") ) );
		addButtonFromActionList(actionListWindows);
	}

	public void addDisplayToggleButtons( ArrayList<IChangeMarkDisplaySendable> boundingBoxToggleUpdateList, MarkDisplaySettings lastMarkDisplaySettings) {
		
		IconFactory rf = new IconFactory();
		
		ArrayList<Action> actionListIndexToggle = new ArrayList<>();
		actionListIndexToggle.add( new ShowBoundingBoxAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_bounding_box.png") ) );
		actionListIndexToggle.add( new ShowInsideAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_inside.png") ) );
		actionListIndexToggle.add( new ShowShellAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_shell.png") ) );
		actionListIndexToggle.add( new ShowMidpointAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_midpoint.png") ) );
		actionListIndexToggle.add( new ShowOrientationLineAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_orientation.png") ) );
		actionListIndexToggle.add( new ShowSolidAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_solid.png") ) );
		actionListIndexToggle.add( new ShowThickBorderAction( boundingBoxToggleUpdateList, lastMarkDisplaySettings, rf.icon("/toolbarIcon/show_thick_border.png") ) );
		addToggleButtonFromActionList(actionListIndexToggle);
	}
	
	
	public VideoStatsToolbar() {
		super();

		//addButtonFromActionList(actionListWindows);
		//addSeparator();
		//addToggleButtonFromActionList(actionListIndexToggle);
	}

	public void add(Action a) {
		delegate.add(a);
	}
	
	public void add(Component component) {
		delegate.add(component);
	}
	
	@Override
	public void removeRefresh(Component component) {
		delegate.remove( component );
		delegate.revalidate();
		delegate.repaint();
	}

	public JToolBar getDelegate() {
		return delegate;
	}

	public void addSeparator() {
		delegate.addSeparator();
	}

	public void add(Component comp, Object constraints) {
		delegate.add(comp, constraints);
	}
	
}
