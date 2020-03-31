package org.anchoranalysis.gui.kernel;

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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.EventListenerList;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.cfgnrg.StatePanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.kernel.match.MatchKernel;
import org.anchoranalysis.gui.reassign.SimpleToggleAction;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.WeightedKernel;

public class KernelIterDescriptionNavigatorPanel extends StatePanel<KernelIterDescription> {

	private JPanel panel;
	
	private JTextArea label;
	private ProposerFailureDescriptionPanel kernelFailurePanel;
	
	private KernelProposer<CfgNRGPixelized> kernelProposer;
	
	private int currentIndex;
	
	private IBoundedIndexContainer<KernelIterDescription> cntr;
	
	private EventListenerList listeners = new EventListenerList();
	
	private IndicesSelection currentSelection =  new IndicesSelection();
	
	private SimpleToggleAction toggleActionReceiveMarks = new SimpleToggleAction("Receive Marks", false);
	
	private KernelMatchPanel choosePanel;
	
	private int loopUntilIndex( int startIndex, boolean forward, MatchKernel match ) throws GetOperationFailedException {
		
		int currentIndex = startIndex;
		while(true) {
			
			currentIndex = forward ? cntr.nextIndex(currentIndex) : cntr.previousIndex(currentIndex);
			
			// No more to go
			if (currentIndex==-1) {
				return -1;
			}
			
			KernelIterDescription kid = cntr.get(currentIndex);
			
			// Does it meet our criteria
			if (match.matches(kid)) {
				return currentIndex;	
			}
		}
	}
	
	public class ResetMarksAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4363275904823016247L;

		public ResetMarksAction() {
	        super("Reset Marks", null);
	    }
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			currentSelection.setCurrentSelection( new int[]{} );
			updateChoosePanelFromCurrentSelection();
		}
		
	}
	
	public class FindKernelAction extends AbstractAction {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -8955032875301982819L;
		private final boolean forward;
		
		public FindKernelAction( String title, boolean forward) {
	        super(title, null);
	        this.forward = forward;
	        //putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK) );
	    }
	    @Override
		public void actionPerformed(ActionEvent e) {
	    	
	    	// We make sure we can no longer receive any marks
	    	toggleActionReceiveMarks.setToggleState( false );
	    	
	    	try {
		    	int index = loopUntilIndex(
					getCurrentIndex(),
					forward, 
					choosePanel.createMatch( currentSelection )
				);
		    	
		    	if (index!=-1) {
		    		sendNewIndex(index);
		    	} else {
		    		JOptionPane.showMessageDialog(panel,"Cannot find a matching frame"); 
		    	}
	    	} catch (GetOperationFailedException exc) {
	    		JOptionPane.showMessageDialog(panel, String.format("Cannot find a matching frame. Exception: %s",exc.toString()) );
	    	}
	    	
	    	
	    }
		
	}
	
	
	public KernelIterDescriptionNavigatorPanel( IBoundedIndexContainer<KernelIterDescription> cntr, KernelProposer<CfgNRGPixelized> kernelProposer ) {
		
		this.kernelProposer = kernelProposer;
		this.cntr = cntr;
		
		init();
	}
	

	private JPanel createFilterPanel() {
		
		// All the different search filters
		JPanel filterPanel = new JPanel();
		
		filterPanel.setLayout( new BorderLayout() );
		
		choosePanel = new KernelMatchPanel( kernelProposer );
		
		JScrollPane scrollPane = new JScrollPane( choosePanel.getPanel() );
		filterPanel.add( scrollPane, BorderLayout.CENTER );

		// Arrows
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new GridLayout(1,2) );
		buttonPanel.add( new JButton(  new FindKernelAction("<", false) ));
		buttonPanel.add( new JButton( new FindKernelAction(">", true) ));
		
		filterPanel.add( buttonPanel, BorderLayout.SOUTH );
		return filterPanel;
	}
	
	
	private void init() {
		
		this.panel = new JPanel();
		
		panel.setBorder( BorderFactory.createEmptyBorder(2, 0, 2, 0) );
		panel.setLayout( new BorderLayout() );

		JPanel topPanel = new JPanel();
		topPanel.setLayout( new GridLayout(1,2) );
		topPanel.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		
		topPanel.add( new JToggleButton(toggleActionReceiveMarks) );
		topPanel.add( new JButton( new ResetMarksAction() ) );
		
		panel.add( topPanel, BorderLayout.NORTH);
		
		this.label = new JTextArea() ;
		this.label.setRows(3);
		
		this.kernelFailurePanel = new ProposerFailureDescriptionPanel(null);
		
		
		JPanel panelBottom = new JPanel();
//		panelBottom.setLayout( new GridLayout(2,1) );
//		panelBottom.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
//		panelBottom.add( this.label );
//		panelBottom.add( this.kernelFailurePanel.getPanel() );
		panelBottom.setLayout( new BorderLayout() );
		panelBottom.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		panelBottom.add( this.label, BorderLayout.NORTH );
		panelBottom.add( this.kernelFailurePanel.getPanel(), BorderLayout.CENTER );
		

		//panelBottom.setPreferredSize( new Dimension(300,100) );
		//panelBottom.setMinimumSize( new Dimension(300,100) );
		
		//this.kernelFailurePanel.setMaximumSize( new Dimension(300,100) );
		
		//panel.add( panelBottom, BorderLayout.SOUTH);

		// All the different search filters
		JPanel filterPanel = createFilterPanel();
		filterPanel.setMinimumSize( new Dimension(300, 200 ));
		
		
		
		//panel.add( filterPanel, BorderLayout.CENTER );
		
		JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, filterPanel, panelBottom );
		//splitPane.setResizeWeight(1.0);
		
		panel.add( splitPane );
		
		
		
		updateChoosePanelFromCurrentSelection();
	}
	
	@SuppressWarnings("unchecked")
	private void sendNewIndex( int index ) {
		
		for ( PropertyValueChangeListener<Integer> event : listeners.getListeners(PropertyValueChangeListener.class)) {
			event.propertyValueChanged( new PropertyValueChangeEvent<>(this, index, false));
		}
	}
	
	private int getCurrentIndex() {
		return currentIndex;
	}

	
	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void updateState(KernelIterDescription state)
		throws StatePanelUpdateException {
		
		this.currentIndex = state.getIndex();
		
		String newline = System.getProperty("line.separator");
		
		WeightedKernel<CfgNRGPixelized> kernelFactory = kernelProposer.getAllKernelFactories().get(state.getId());
		
		StringBuilder s = new StringBuilder();
		
		if (!state.getDescription().isEmpty()) {
			s.append( state.getDescription() );
		} else {
			s.append( kernelFactory.toString() );
		}
		
		s.append( newline );
		
		s.append( String.format("%d ms", state.getExecutionTime() ));
		
		s.append( newline );
		
		if (!state.isProposed()) {
			s.append("not proposed");
		} else {
		
			if (state.isAccepted()) {
				s.append( "accepted" );
			} else {
				s.append( "rejected");
			}
		}
		this.label.setText( s.toString() );
		
		kernelFailurePanel.updateState( state.getNoProposalReason() );
		
		
		 
		// Scroll to top of the text
		//this.label.setCaretPosition(0);
		//this.label.scrollRectToVisible(new Rectangle(0,0,2, 2));
		//JViewport jv = this.labelScrollPane.getViewport();  
		//jv.setViewPosition(new Point(0,0)); 
	}

	private void updateChoosePanelFromCurrentSelection() {
		
		if (!currentSelection.isEmpty()) {
			choosePanel.setMarksText( currentSelection.toString() );
		} else {
			choosePanel.setMarksText( "any");
		}
	}
	
	@Override
	public IPropertyValueSendable<IntArray> getSelectMarksSendable() {
		return new IPropertyValueSendable<IntArray>() {

			@Override
			public void setPropertyValue(IntArray value, boolean adjusting) {
				
				if (!toggleActionReceiveMarks.getToggleState()) {
					return;
				}
				
				currentSelection.setCurrentSelection( value.getArr() );
				updateChoosePanelFromCurrentSelection();
			}
		};
	}


	@Override
	public IPropertyValueReceivable<IntArray> getSelectMarksReceivable() {
		return null;
	}


	@Override
	public IPropertyValueReceivable<OverlayCollection> getSelectOverlayCollectionReceivable() {
		return null;
	}


	@Override
	public IPropertyValueReceivable<Integer> getSelectIndexReceivable() {
		return new IPropertyValueReceivable<Integer>() {

			@Override
			public void addPropertyValueChangeListener(PropertyValueChangeListener<Integer> changeListener) {
				listeners.add( PropertyValueChangeListener.class, changeListener );
			}

			@Override
			public void removePropertyValueChangeListener(PropertyValueChangeListener<Integer> changeListener) {
				listeners.remove( PropertyValueChangeListener.class, changeListener);
			}
		};
	}
}
