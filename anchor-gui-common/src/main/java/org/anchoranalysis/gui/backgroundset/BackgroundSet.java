package org.anchoranalysis.gui.backgroundset;

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


import java.util.HashMap;
import java.util.Set;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.functional.IdentityOperation;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.BoundedIndexBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.gui.container.background.CombineRGBBackgroundStackCntr;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;

/**
 * A related set of backgrounds available to modules
 * Assumes they all have the same size.
 */
 public class BackgroundSet {

	private HashMap<
		String,
		Operation<BackgroundStackCntr,OperationFailedException>
	> map = new HashMap<>();
	
	public BackgroundSet() {
		
	}
	
	public void addAll( BackgroundSet src ) {
		for( String srcItem : src.map.keySet() ) {
			addItem( srcItem, src.getItem(srcItem) );
		}
	}

	public void addItem( String name, BackgroundStackCntr rasterBackground ) {
		addItem(
			name,
			new IdentityOperation<>(rasterBackground) 
		); 
	}
	
	public void addItem( String name, final Stack stack) throws OperationFailedException {
		addItem( name, BackgroundStackCntrFactory.singleSavedStack(stack) );
	}

	public void addItem( String name, Operation<BackgroundStackCntr,OperationFailedException> rasterBackground ) {
		map.put(name, rasterBackground );
	}
	
	public BackgroundStackCntr getItem( String name ) {
		try {
			return map.get(name).doOperation();
		} catch (OperationFailedException e) {
			assert false;
			return null;
		}
	}
	
	
	public DisplayStack singleStack( String name ) throws GetOperationFailedException {
		
		try {
			BackgroundStackCntr finderRaster = map.get(name).doOperation();
			
			if (finderRaster==null) {
				return null;
			}
			
			return finderRaster.backgroundStackCntr().get(0);
		} catch (OperationFailedException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	// Gives us a stack container for a particular name, or NULL if none exists
	// NOTE: There is only a mapping between 0 and a single image
	public FunctionWithException<Integer,DisplayStack,GetOperationFailedException> stackCntr( String name ) throws GetOperationFailedException {
		try {
			
			Operation<BackgroundStackCntr,OperationFailedException> op = map.get(name);
			
			if (op==null) {
				return null;
			}
			
			BackgroundStackCntr backgroundStackCntr = op.doOperation();
			
			if (backgroundStackCntr==null) {
				return null;
			}
			
			return new BoundedIndexBridge<>(
				backgroundStackCntr.backgroundStackCntr()
			);
		} catch (OperationFailedException e) {
			throw new GetOperationFailedException(e);
		}
	}
	
	public Set<String> names() {
		return map.keySet();
	}
	
	public void addCombined( String top, String bottom) throws GetOperationFailedException, InitException {
		CombineRGBBackgroundStackCntr combined = new CombineRGBBackgroundStackCntr();
		
		BackgroundStackCntr red = getItem(bottom);
		BackgroundStackCntr green = getItem(top);
		
		assert( red!=null && green != null );
		
		combined.setRed( red );
		combined.setGreen( green );
		addItem(top + " on " + bottom, combined.create() );
	}
}
