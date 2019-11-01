package org.anchoranalysis.gui.cfgnrgtable;

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
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.core.property.PropertyValueReceivableFromIndicesSelection;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.cfgnrg.StatePanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;
import org.anchoranalysis.gui.indices.DualIndicesSelection;
import org.anchoranalysis.gui.mark.CfgUtilities;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;

import overlay.OverlayCollectionMarkFactory;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public class CfgNRGTablePanel extends StatePanel<CfgNRGInstantState> {

	private JPanel panelBig;
	
	private ArrayList<IUpdateTableData> updateList = new ArrayList<>();
	private PairTablePanel pairPanel;
	private IndividualTablePanel individualPanel;

	private DualIndicesSelection selectionIndices = new DualIndicesSelection();
	
	private CfgNRGInstantState state;
	
	private NRGStackWithParams associatedRaster;
	
	private PropertyValueChangeListenerList<OverlayCollection> eventListenerListOverlayCollection = new PropertyValueChangeListenerList<>();
	private PropertyValueChangeListenerList<OverlayCollectionWithImgStack> eventListenerListOverlayCollectionWithStack = new PropertyValueChangeListenerList<>();
	
	private class ClickAdapterIndividual extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent arg0) {
			super.mouseReleased(arg0);
			
			// This event always seems to happen after any ListSelection events, so we rely
			//   on it to occur, on each occasion we want to send a ObjChangeEvent for clicking
			// on a mark
			
			selectionIndices.updateBoth( individualPanel.getSelectedIDs() );
			pairPanel.getSelectMarksSendable().selectIndicesOnly(  individualPanel.getSelectedIDs() );
			
			//System.out.println( "Setting current selection (Ind)" + selectionIndices.toString() );
			
			// Crucially the selectionIndices must be updated with the ListSelection change, before it occurs
			//  we assume this happens
			triggerEvent();
		}
	}
	
	private class ClickAdapterPair extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent arg0) {
			super.mouseReleased(arg0);
			
			// This event always seems to happen after any ListSelection events, so we rely
			//   on it to occur, on each occasion we want to send a ObjChangeEvent for clicking
			// on a mark
			
			selectionIndices.updateBoth( pairPanel.getSelectedIDs() );
			individualPanel.getSelectMarksSendable().selectIndicesOnly(  pairPanel.getSelectedIDs() );
			//System.out.println( "Setting current selection (Pair)" + selectionIndices.toString() );
			
			// Crucially the selectionIndices must be updated with the ListSelection change, before it occurs
			//  we assume this happens
			triggerEvent();
		}
	}

		
	
	public CfgNRGTablePanel(
		ColorIndex colorIndex,
		NRGStackWithParams associatedRaster
	) {
		super();
		
		this.associatedRaster = associatedRaster;
		
		final SummaryTablePanel summaryPanel = new SummaryTablePanel();
		individualPanel = new IndividualTablePanel(colorIndex, selectionIndices.getCurrentSelection() );
		pairPanel = new PairTablePanel(colorIndex, selectionIndices.getCurrentSelection() );
		
		ClickAdapterIndividual ca = new ClickAdapterIndividual();
		individualPanel.addMouseListener(ca);
		pairPanel.addMouseListener( new ClickAdapterPair() );
		
		this.panelBig = new JPanel();
		
		panelBig.setBorder( BorderFactory.createEmptyBorder(2, 0, 2, 0) );
		panelBig.setLayout( new BorderLayout() );
		
		panelBig.add( summaryPanel.getPanel(), BorderLayout.NORTH);
		
		JPanel panelScroll = new JPanel();
		panelScroll.setBorder( BorderFactory.createEmptyBorder(2, 0, 2, 0) );
		
		panelScroll.setLayout( new GridLayout(2,1) );
		panelScroll.add( individualPanel.getPanel() );
		panelScroll.add( pairPanel.getPanel() );
		
		panelBig.add(panelScroll, BorderLayout.CENTER);
		
		updateList.add( summaryPanel.getUpdateTableData() );
		updateList.add( individualPanel.getUpdateTableData() );
		updateList.add( pairPanel.getUpdateTableData() );
	}
	
	
	private void triggerEvent() {
		Cfg cfg = state.getCfgNRG() != null ? state.getCfgNRG().getCfg() : null;
		if (cfg!=null) {
			Cfg cfgSubset = CfgUtilities.createCfgSubset( cfg, selectionIndices.getCurrentSelection() );
			RegionMembershipWithFlags regionMembership = RegionMapSingleton.instance().membershipWithFlagsForIndex( GlobalRegionIdentifiers.SUBMARK_INSIDE );
			OverlayCollection overlaySubset = OverlayCollectionMarkFactory.createWithoutColor(
				cfgSubset,
				regionMembership
			);
			triggerObjectChangeEvent( overlaySubset );
		}
	}
	
	@Override
	public JPanel getPanel() {
		return panelBig;
	}
	
	@Override
	public void updateState( CfgNRGInstantState state ) throws StatePanelUpdateException {
		
		this.state = state;
		
		// We do this before updateTableData as it will change the selection
		//int[] selectionPrevious = selectionIndices.getSelectedIDs();
		
		for (IUpdateTableData updater : updateList) {
			updater.updateTableData(state);
		}
		
		individualPanel.getSelectMarksSendable().selectIndicesOnly( selectionIndices.getCurrentSelection().getCurrentSelection() );
		pairPanel.getSelectMarksSendable().selectIndicesOnly( selectionIndices.getCurrentSelection().getCurrentSelection() );
	}
	
	
	private void triggerObjectChangeEvent( OverlayCollection state ) {
		for ( PropertyValueChangeListener<OverlayCollection> l : eventListenerListOverlayCollection) {
			l.propertyValueChanged( new PropertyValueChangeEvent<>(this, state, false) );
		}
		
		for ( PropertyValueChangeListener<OverlayCollectionWithImgStack> l : eventListenerListOverlayCollectionWithStack) {
			l.propertyValueChanged( new PropertyValueChangeEvent<>(this, new OverlayCollectionWithImgStack(state, associatedRaster), false) );
		}

	}
	
	@Override
	public IPropertyValueSendable<IntArray> getSelectMarksSendable() {
		return new IPropertyValueSendable<IntArray>() {

			@Override
			public void setPropertyValue(IntArray value, boolean adjusting) {
				
				int[] ids = value.getArr();
				
				selectionIndices.setCurrentSelection(ids);
				//System.out.println( "Setting current selection (IF)" + selectionIndices.toString() );
				
				individualPanel.getSelectMarksSendable().selectIndicesOnly( ids );
				pairPanel.getSelectMarksSendable().selectIndicesOnly( ids );
				
			}
		};
	}
	
	@Override
	public IPropertyValueReceivable<OverlayCollection> getSelectOverlayCollectionReceivable() {
		return eventListenerListOverlayCollection.createPropertyValueReceivable();
	}

	@Override
	public IPropertyValueReceivable<IntArray> getSelectMarksReceivable() {
		return new PropertyValueReceivableFromIndicesSelection(selectionIndices.getLastExplicitSelection());
	}


	@Override
	public IPropertyValueReceivable<Integer> getSelectIndexReceivable() {
		return null;
	}


	
	
	
}
