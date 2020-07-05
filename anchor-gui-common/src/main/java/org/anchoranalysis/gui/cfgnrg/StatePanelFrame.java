package org.anchoranalysis.gui.cfgnrg;

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


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JInternalFrame;

import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

/**
 * A frame that encompasses a StatePanel (updated each time state of type T changes) 
 * 
 * @author Owen Feehan
 *
 * @param <T>
 */
public class StatePanelFrame<T> {

	private JInternalFrame frame;
		
	private StatePanel<T> tablePanel;
	
	private ControllerSize controllerSize;
	
	public StatePanelFrame( String title, T initialState, StatePanel<T> tablePanel ) throws StatePanelUpdateException {
		
		this.tablePanel = tablePanel;
		
		//System.out.println("Updating initial state");
		updateState(initialState);
		
		frame = new JInternalFrame(title);
		frame.setResizable(true);
		frame.setMaximizable(true);
		frame.setIconifiable(true);
		frame.setClosable(true);

		frame.add(tablePanel.getPanel(), BorderLayout.CENTER);
		
		frame.setPreferredSize( new Dimension(280, (int) frame.getPreferredSize().getHeight() ));
		frame.setVisible(true);
				
		updateTitle(title);
		
		controllerSize = new ControllerSize() {
			
			@Override
			public void setPreferredSize(Dimension arg0) {
				frame.setPreferredSize(arg0);
			}

			@Override
			public void setMinimumSize(Dimension arg0) {
				frame.setMinimumSize(arg0);
			}
		};
	}
		
	// Get frame
	public JInternalFrame getFrame() {
		return frame;
	}
	
	public ControllerSize controllerSize() {
		return controllerSize;
	}

	public void addFrameChangeListener(PropertyValueChangeListener<Integer> changeListener) {
		tablePanel.getSelectIndexReceivable().ifPresent( index->
			index.addPropertyValueChangeListener(changeListener)
		);
	}

	public void removeFrameChangeListener(PropertyValueChangeListener<Integer> changeListener) {
		tablePanel.getSelectIndexReceivable().ifPresent( index->
			index.removePropertyValueChangeListener(changeListener)	
		);
	}
	
	public void updateState( T state ) throws StatePanelUpdateException {
		tablePanel.updateState(state);
	}
	
	public void updateTitle( String title ) {
		frame.setTitle(  title );
	}
	
	public IModuleCreatorDefaultState moduleCreator() {
		return defaultFrameState-> {
		
			VideoStatsModule module = new VideoStatsModule();
			
			module.setComponent( frame );
			module.setFixedSize(false);
	
			LinkModules link = new LinkModules(module);
			link.getMarkIndices().add(
				tablePanel.getSelectMarksReceivable(),
				tablePanel.getSelectMarksSendable()
			);
			link.getOverlays().add(
				tablePanel.getSelectOverlayCollectionReceivable()
			);
			return module;
		};
	}

}
