package org.anchoranalysis.gui.mergebridge;

import java.util.ArrayList;
import java.util.List;


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


import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.cache.LRUHashMapCache;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundChangeListener;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;

import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;

// Contains both the selected and proposal histories
public class DualCfgNRGContainer<T> implements IBoundedIndexContainer<IndexedDualState<T>> {
	
	// Selected CfgNRG
	private List<IBoundedIndexContainer<CfgNRGInstantState>> cntrs;
	
	private IncrementalSequenceType incrementalSequenceType;
	
	private LRUHashMapCache<IndexedDualState<T>,Integer> recentAccessCache;
	
	@FunctionalInterface
	public interface TransformInstanteState<T> {
		T transform( CfgNRGInstantState state );
	}
		
	private TransformInstanteState<T> transformer;
	
	/**
	 * 
	 * @param cntrs assumed to represent a contiguous sequence in time
	 * @param transformer
	 */
	public DualCfgNRGContainer( List<IBoundedIndexContainer<CfgNRGInstantState>> cntrs, TransformInstanteState<T> transformer ) throws GetOperationFailedException {
		super();
		
		this.cntrs = cntrs;
		
		this.transformer = transformer;
		
	}

	
	public void init( CacheMonitor cacheMonitor ) throws InitException {

		this.recentAccessCache = LRUHashMapCache.createAndMonitor(
			3,
			index -> new IndexedDualState<T>(
				index,
				instanceStates(index)
			),
			cacheMonitor,
			"DualCfgNRGContainer"
		);
		
		this.incrementalSequenceType = new IncrementalSequenceType();
		this.incrementalSequenceType.setStart( maxOfMins() );
		this.incrementalSequenceType.setIncrementSize( 1);
		this.incrementalSequenceType.setEnd( minOfMaxs() );
	}

	@Override
	public void addBoundChangeListener(BoundChangeListener cl) {
		// We treat as static			
	}

	@Override
	public int nextIndex(int index) {
		return this.incrementalSequenceType.nextIndex(index);
	}

	@Override
	public int previousIndex(int index) {
		return this.incrementalSequenceType.previousIndex(index);
	}
	

	@Override
	public int previousEqualIndex(int index) {
		return this.incrementalSequenceType.previousEqualIndex(index);
	}

	@Override
	public int getMinimumIndex() {
		return this.incrementalSequenceType.getMinimumIndex();
	}

	@Override
	public int getMaximumIndex() {
		return this.incrementalSequenceType.getMaximumIndex();
	}

	@Override
	public IndexedDualState<T> get(int index) throws GetOperationFailedException {
		return recentAccessCache.get(index);
	}
	
	private int maxOfMins() {
		
		int maxOfMins = Integer.MIN_VALUE;
		
		for( IBoundedIndexContainer<CfgNRGInstantState> cntr : cntrs ) {
			
			if (cntr.getMinimumIndex()>maxOfMins) {
				maxOfMins = cntr.getMinimumIndex();
			}
		}
		
		return maxOfMins;
	}
	
	private int minOfMaxs() {
		
		int minOfMaxs = Integer.MAX_VALUE;
		
		for( IBoundedIndexContainer<CfgNRGInstantState> cntr : cntrs ) {
			
			if (cntr.getMaximumIndex()<minOfMaxs) {
				minOfMaxs = cntr.getMaximumIndex();
			}
		}
		
		return minOfMaxs;
	}
	

	private List<T> instanceStates(int index) throws GetOperationFailedException {
		
		List<T> out = new ArrayList<T>();
		
		for( int i=0; i< cntrs.size(); i++ ) {
			T item = transformer.transform(
				createNearestState( cntrs.get(i), index)
			);
			out.add( item );
		}
		
		return out;
	}
	
	private static CfgNRGInstantState createNearestState( IBoundedIndexContainer<CfgNRGInstantState> cntr, int index ) throws GetOperationFailedException {
		int selectedIndex = cntr.previousEqualIndex(index);
		return cntr.get(selectedIndex);
	}
}