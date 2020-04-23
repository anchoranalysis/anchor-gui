package org.anchoranalysis.gui.feature.evaluator.treetable;

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


import java.awt.CardLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.calc.params.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.netbeans.swing.outline.Outline;

// Has a number of different tables for cliques sizes (0, 1, 2) all of which remain memory, but only one
//   of which is visible at any given time
public class TreeTableWithModelMultiplex implements ITreeTableModel {

	private List<TreeTableWithModel> list = new ArrayList<>();
	private JPanel panel = new JPanel();
	private CardLayout cardLayout =  new CardLayout();
	
	private int currentlySelectedMode = 0;
	
	public TreeTableWithModelMultiplex( TreeTableProperties properties,	FeatureListSrc featureListExtracter ) {
		super();
		
		FeatureListWithRegionMap<FeatureInputSingleMemo>  featureListInd = featureListExtracter.createInd();
		FeatureListWithRegionMap<FeatureInputPairMemo>  featureListPair = featureListExtracter.createPair();
		FeatureListWithRegionMap<FeatureInputAllMemo> featureListAll = featureListExtracter.createAll();
		SharedFeatureSet<FeatureInputNRGStack> sharedFeatures = featureListExtracter.sharedFeatures();
		
		// We use 4 models for NONE, IND, PAIR, ALL
		addModelToList( new FeatureListWithRegionMap<FeatureInputNRGStack>(), properties, sharedFeatures );
		addModelToList( featureListInd, properties, sharedFeatures );
		addModelToList( featureListPair, properties, sharedFeatures );
		addModelToList( featureListAll, properties, sharedFeatures );
		
		panel.setLayout( cardLayout );
		panel.setBorder( BorderFactory.createEmptyBorder(2, 0, 2, 0) );
		
		int i = 0;
		for( TreeTableWithModel item : list ) {
			panel.add( item.getComponent(), Integer.toString(i++) );
		}
	}

	private <T extends FeatureInputNRGStack> void addModelToList(
		FeatureListWithRegionMap<T> features,
		TreeTableProperties properties,
		SharedFeatureSet<FeatureInputNRGStack> sharedFeatures
	) {
		list.add( new TreeTableWithModel(properties, features.upcast(), sharedFeatures.upcast()) );
	}
	
	public void resizeColumns() {
		
		for (TreeTableWithModel item : list) {
			item.resizeColumns();
		}
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}
	
	// We should do this to all our tables
	@Override
	public void addMouseListenerToOutline( MouseListener ml ) {
		for (TreeTableWithModel item : list) {
			item.getOutline().addMouseListener(ml);
		}
	}

	@Override
	public Outline getOutline() {
		return list.get(currentlySelectedMode).getOutline();
	}

	@Override
	public void updateSingle(Overlay overlay, NRGStackWithParams raster) {
		
		if (overlay==null) {
			selectMode(0);
			list.get(0).updateSingle(null,null);
			return;
		}
		
		//System.out.println("Updating mark clique size 1");
		
		selectMode(1);
		list.get(1).updateSingle(overlay, raster);
		
	}

	@Override
	public void updatePair(Pair<Overlay> pair, NRGStackWithParams raster) {
		
		if (pair==null) {
			selectMode(0);
			list.get(0).updateSingle(null,null);
			return;
		}
		
		//System.out.println("Updating mark clique size 2");
		
		selectMode(2);
		list.get(2).updatePair(pair, raster);
	}
	
	
	

	private void selectMode( int index ) {
		currentlySelectedMode = index;
		cardLayout.show(panel, Integer.toString(index) );
	}
}
