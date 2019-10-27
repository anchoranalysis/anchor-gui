package org.anchoranalysis.gui.videostats.internalframe.annotator.navigation;

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


import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.EventListenerList;

import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.CurrentStateDisplayer;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.AnnotationTool;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.DeleteTool;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.GuessTool;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.SelectPointsTool;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

import ch.ethz.biol.cell.mpp.mark.pointsfitter.PointsFitter;

// A panel for selecting tools
public class PanelTool extends PanelWithLabel implements ISwitchToGuessOrSelectPoints, IToolPanelListenFramework {

	private JToggleButton buttonToolGuess;
	private JToggleButton buttonToolSelectPoints;
	private JToggleButton buttonToolDelete;
	
	private GuessTool toolGuess;
	private SelectPointsTool toolSelectPoints;
	private DeleteTool toolDelete;
	
	private EventListenerList eventListeners = new EventListenerList();
	
	// Whether we return to Guess or SelectPoints after we finish with delete
	private boolean isGuessReturnToSelection = true;
		
	public PanelTool(
		CurrentStateDisplayer currentStateDisplayer,
		EvaluatorWithContext evaluatorGuess,
		EvaluatorWithContext evaluatorSelectPoints,
		PointsFitter pointsFitterSelectPoints,
		ToolErrorReporter errorReporter
	) {
		buttonToolGuess = createNonFocussableButton("Guess");
		buttonToolSelectPoints = createNonFocussableButton("Points");
		buttonToolDelete = createNonFocussableButton("Delete");
		
		addButtonChanged();
				
		toolGuess = new GuessTool(
			currentStateDisplayer.replaceRemove(),
			currentStateDisplayer.confirmReset(),
			evaluatorGuess,
			errorReporter
		);
		selectGuessTool();
		toolSelectPoints = new SelectPointsTool(
			evaluatorSelectPoints,
			currentStateDisplayer.changeSelectedPoints(),
			pointsFitterSelectPoints,
			currentStateDisplayer.confirmReset(),
			currentStateDisplayer.querySelectedPoints(),
			errorReporter
		);
		toolDelete = new DeleteTool(
			currentStateDisplayer.queryAcceptReject(),
			currentStateDisplayer.querySelectedPoints(),
			currentStateDisplayer.replaceRemove(),
			this
		);
				
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(buttonToolGuess);
		buttonGroup.add(buttonToolSelectPoints);
		buttonGroup.add(buttonToolDelete);
		
		super.init("Mouse Pointer");
	}
		
	public void dispose() {
		getPanel().removeAll();
		buttonToolGuess = null;
		buttonToolSelectPoints = null;
		buttonToolDelete = null;
		eventListeners = null;
		toolGuess = null;
		toolSelectPoints = null;
		toolDelete = null;
	}
	
	private static JToggleButton createNonFocussableButton( String title ) {
		return new JToggleButton(title);
	}
	
	private void addButtonChanged() {
		ButtonChanged buttonChanged = new ButtonChanged();
		buttonToolGuess.addActionListener(buttonChanged);
		buttonToolSelectPoints.addActionListener(buttonChanged);
		buttonToolDelete.addActionListener(buttonChanged);
	}
	
	private void selectGuessTool() {
		if (toolGuess.isEnabled()) {
			buttonToolGuess.setSelected(true);
		} else {
			buttonToolGuess.setEnabled(false);
			buttonToolSelectPoints.setSelected(true);
		}
	}
	
	public void switchToGuess() {
		
		if (!toolGuess.isEnabled()) {
			switchToSelectPoints();
		}
		
		buttonToolGuess.setSelected(true);
		isGuessReturnToSelection = true;
		throwToolSwitchedEvent();
	}
	
	public void switchToSelectPoints() {
		buttonToolSelectPoints.setSelected(true);
		isGuessReturnToSelection = false;
		throwToolSwitchedEvent();
	}
	
	public void switchToDelete() {
		buttonToolDelete.setSelected(true);
		throwToolSwitchedEvent();
	}
	
	// Switches to either Guess or SelectPoints based on whatever was chosen previously
	@Override
	public void switchToGuessOrSelectPoints() {
		
		if (!toolGuess.isEnabled()) {
			switchToSelectPoints();
		}
		
		
		if (isGuessReturnToSelection) {
			switchToGuess();
		} else {
			switchToSelectPoints();
		}
	}
	
	public JMenu createSwitchToolMenu() {
		return new SwitchToolMenu(this).getMenu();
	}
	
	public void switchRotateLeft() {
		if (isSelectedGuess()) {
			switchToDelete();
		} else if (isSelectedDelete()) {
			switchToSelectPoints();
		} else {
			switchToGuess();
		}
	}
	
	public void switchRotateRight() {
		if (isSelectedGuess()) {
			switchToSelectPoints();
		} else if (isSelectedSelectPoints()) {
			switchToDelete();
		} else {
			switchToGuess();
		}
	}
	
	public boolean isSelectedGuess() {
		return buttonToolGuess.isSelected();
	}
	
	public boolean isSelectedSelectPoints() {
		return buttonToolSelectPoints.isSelected();
	}
	
	@Override
	public boolean isSelectedDelete() {
		return buttonToolDelete.isSelected();
	}
	
	public AnnotationTool getTool() {
		if( buttonToolGuess.isSelected() ) {
			return toolGuess;
		} else if (buttonToolSelectPoints.isSelected()){
			return toolSelectPoints;
		} else if (buttonToolDelete.isSelected()){
			return toolDelete;
		} else {
			assert false;		// One of these should always be selected
			return null;
		}
	}

	@Override
	public void addToolSwitchedListener(ToolSwitchedListener l) {
		eventListeners.add(ToolSwitchedListener.class, l);
	}
	
	@Override
	protected JPanel createMainPanel() {
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout(1, 3));
		
		panel.add( buttonToolGuess );
		panel.add( buttonToolSelectPoints );
		panel.add( buttonToolDelete );
		
		return panel;
	}
	
	private class ButtonChanged implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// If it's not Delete that has been selected
			//  we update our ReturnTo
			if(!isSelectedDelete()) {
				isGuessReturnToSelection = isSelectedGuess();
			}
			
			throwToolSwitchedEvent();
			
		}
		
	}
	
	private void throwToolSwitchedEvent() {
		for( ToolSwitchedListener l : eventListeners.getListeners(ToolSwitchedListener.class) ) {
			l.toolSwitched();
		}
	}

}
