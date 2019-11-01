package org.anchoranalysis.gui.feature.evaluator;

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
import java.util.Collections;

import org.anchoranalysis.anchor.mpp.mark.OverlayProperties;
import org.anchoranalysis.core.name.value.ComparatorOrderByName;
import org.anchoranalysis.core.name.value.INameValue;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.cfgnrgtable.TitleValueTableModel;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;
import org.anchoranalysis.image.extent.ImageRes;

import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.mpp.pair.Pair;

class OverlayDescription extends TitleValueTableModel implements IUpdatableSinglePair {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5093139154944903750L;
	
	public OverlayDescription() {
		
	}
	
	@Override
	public void updateSingle( final Overlay overlay, NRGStackWithParams raster ) {
		clear();
		
		// If we have no mark matching the current id
		if (overlay==null) {
			fireTableDataChanged();
			return;
		}
		
		ImageRes sr = raster.getDimensions()!= null ? raster.getDimensions().getRes() : null;
		addOverlayDetails( overlay, "", sr );
		
		fireTableDataChanged();
	}
	
	@Override
	public void updatePair( final Pair<Overlay> pair, NRGStackWithParams raster ) {
		
		clear();
		// If we have no mark matching the current id
		if (pair==null) {
			fireTableDataChanged();
			return;
		}
		
		addEntry( new SimpleTitleValue( "Pair",  pair.toString()  ) );
		
		addOverlayDetails( pair.getSource(), "Source: ", raster.getDimensions().getRes() );
		addOverlayDetails( pair.getDestination(), "Dest: ", raster.getDimensions().getRes() );
		
		fireTableDataChanged();
	}
	
	
	private void addOverlayDetails( Overlay overlay, String titlePrefix, ImageRes sr ) {

		OverlayProperties op = overlay.generateProperties(sr);
		
		ArrayList<INameValue<String>> listToAdd = new ArrayList<>(); 
		for (INameValue<String> nv : op) {
			listToAdd.add(nv);
		}
		
		Collections.sort(listToAdd, new ComparatorOrderByName<String>() );
		
		for (INameValue<String> nv : listToAdd) {
			addEntry( new SimpleTitleValue( titlePrefix + nv.getName(), nv.getValue()) );
		}
	}
}