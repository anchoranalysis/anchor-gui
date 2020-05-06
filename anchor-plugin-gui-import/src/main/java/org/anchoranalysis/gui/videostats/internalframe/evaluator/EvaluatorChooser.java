package org.anchoranalysis.gui.videostats.internalframe.evaluator;

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
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorRslvd;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.ProposalOperationCreatorFromProposer;

public class EvaluatorChooser {

	private JPanel panel = new JPanel();

	private JComboBox<String> comboType;
	private JComboBox<String> comboProposer;
	private JComboBox<String> comboMarkEvaluator;
	
	private MarkEvaluatorSetForImage markEvaluatorSet;
	
	private ProposalOperationCreator evaluator;
	
	private List<ProposalOperationCreatorFromProposer<?>> listEvaluators = new ArrayList<>(); 
	private ErrorReporter errorReporter;
	
	private MarkEvaluatorRslvd markEvaluatorSelected;
	
	private EventListenerList eventListenerList = new EventListenerList();
	
	
	
	public EvaluatorChooser( List<ProposalOperationCreatorFromProposer<?>> listEvaluators, final ErrorReporter errorReporter ) {
		this.errorReporter = errorReporter;
		this.listEvaluators = listEvaluators;
		
		
		comboMarkEvaluator = new JComboBox<>();
	
		comboType = new JComboBox<>();
		
		for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
			comboType.addItem( item.getEvaluatorName() );
		}
		
		comboProposer = new JComboBox<>();
		
		
		panel.add(comboMarkEvaluator);
		panel.add(comboType);
		panel.add(comboProposer);
		

		comboMarkEvaluator.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectMarkEvaluator();
				
			}
		});
		comboType.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				populateComboProposer();
			}
		});
		comboProposer.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				
				String itemName = (String) cb.getSelectedItem();
				
				if (itemName==null) {
					return;
				}
				
				try {
					evaluator = createProposerEvaluator(itemName);
				} catch (CreateException e1) {
					errorReporter.recordError(EvaluatorChooser.class, e1);
				}
			}
		});
		
	}
		
	public void init( MarkEvaluatorSetForImage markEvaluatorSet ) {

		this.markEvaluatorSet = markEvaluatorSet;
		
		comboMarkEvaluator.addItem( "" );
		for (String key : markEvaluatorSet.keySet()) {
			comboMarkEvaluator.addItem( key );
		}
		

		selectMarkEvaluator();
	}

	public void selectMarkEvaluator() {

		
		populateComboProposer();
		

		
	}
	
	
	private String getEvaluatorName() {
		return (String) comboMarkEvaluator.getSelectedItem();
	}
	
	public void populateComboProposer() {

		comboProposer.removeAllItems();
		
		String evaluatorName = getEvaluatorName();

		if (evaluatorName==null || evaluatorName.isEmpty()) {
			markEvaluatorSelected = null;
			evaluator = null;
			fireMarkEvaluatorChangedEvent();
			return;
		}
		

		String typeName = (String) comboType.getSelectedItem();
		
		// If it's called before the init
		if (typeName==null) {
			markEvaluatorSelected = null;
			evaluator = null;
			fireMarkEvaluatorChangedEvent();
			return;
		}
		
		try {
			markEvaluatorSelected = markEvaluatorSet.get(evaluatorName);
		} catch (GetOperationFailedException e1) {
			errorReporter.recordError(EvaluatorChooser.class, e1);
		}
		
		for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
			try {
				MPPInitParams so = markEvaluatorSelected.getProposerSharedObjectsOperation().doOperation();
				item.init( so );
			} catch (CreateException e) {
				errorReporter.recordError(EvaluatorChooser.class, e);
			}
		}
		
		for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
			
			if (typeName.equals(item.getEvaluatorName())) {
				for (String name : item.keys() ) {
					comboProposer.addItem( name );
				}
			}
		}
		
		fireMarkEvaluatorChangedEvent();
	}
	
	private void fireMarkEvaluatorChangedEvent() {
		MarkEvaluatorChangedEvent event = new MarkEvaluatorChangedEvent(this,markEvaluatorSelected, getEvaluatorName() );
	    for(MarkEvaluatorChangedListener l : eventListenerList.getListeners(MarkEvaluatorChangedListener.class)) {
	    	l.markEvaluatorChanged(event);
	    }
	}
	

	public synchronized void addMarkEvaluatorChangedListener(MarkEvaluatorChangedListener listener)  {
		eventListenerList.add(MarkEvaluatorChangedListener.class, listener);
	}
	
	public synchronized void removeEventListener(MarkEvaluatorChangedListener listener)   {
		eventListenerList.remove(MarkEvaluatorChangedListener.class, listener);
	}
	 
	public ProposalOperationCreator createProposerEvaluator( String itemName ) throws CreateException {
		
		String typeName = (String) comboType.getSelectedItem();
		
		for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
			if (typeName.equals(item.getEvaluatorName())) {
				return item.createEvaluator(itemName);
			}
		}
		
		assert false;
		return null;
	}
	
	public JPanel getPanel() {
		return panel;
	}

	public ProposalOperationCreator evaluator() {
		return evaluator;
	}
	
	
	public EvaluatorWithContextGetter evaluatorWithContext() {
		return () -> {
			return new EvaluatorWithContext(
				evaluator,
				evaluator!=null ? markEvaluatorSelected.getNRGStack() : null,
				evaluator!=null ? cfgGen() : null,
				evaluator!=null ? regionMap() : null
			);
		};
	}
	
	private CfgGen cfgGen() {
		if (markEvaluatorSelected!=null) {
			return markEvaluatorSelected.getCfgGen();
		} else {
			return null;
		}
	}
	
	private RegionMap regionMap() {
		if (markEvaluatorSelected!=null) {
			return markEvaluatorSelected.getNrgScheme().getRegionMap();
		} else {
			return null;
		}
	}
}
