package org.anchoranalysis.gui.videostats.link;

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

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;
import org.anchoranalysis.image.stack.DisplayStack;

/**
 * When a new module is created, this sets a sensible default value for all of these linked
 *  properties based upon other open entities, or the last value that was previously used
 *  
 * This provides the only write access to DefaultLinkState so that the code can carefully
 *  control all operations that change the default state 
 */
public class DefaultLinkStateManager {

	private DefaultLinkState delegate;

	@SuppressWarnings("rawtypes")
	private HashMap<String,IPropertyValueSendable> mapSendableProperties = new HashMap<>();
		
	public DefaultLinkStateManager( DefaultLinkState state ) {
		this.delegate = state;
		
		this.<Integer>putMap(
			LinkFramesUniqueID.SLICE_NUM,
			(value,adjusting)->delegate.setSliceNum(value)
		);
		
		this.<Integer>putMap(
			LinkFramesUniqueID.FRAME_INDEX,
			(value,adjusting)->delegate.setFrameIndex(value)
		);
		
		this.<IntArray>putMap(
			LinkFramesUniqueID.MARK_INDICES,
			(value,adjusting)->delegate.setObjectIDs(value.getArr())
		);
	
		this.<OverlayCollection>putMap(
			LinkFramesUniqueID.OVERLAYS,
			(value,adjusting)->delegate.setOverlayCollection(value)
		);
		
		this.<OverlayCollectionWithImgStack>putMap(
			LinkFramesUniqueID.OVERLAYS_WITH_STACK,
			(value,adjusting)->delegate.setCfgWithStack(value)
		);
	}
	
	@SuppressWarnings("unchecked")
	public IPropertyValueSendable<Integer> getSendableSliceNum() {
		return (IPropertyValueSendable<Integer>) getSendable(LinkFramesUniqueID.SLICE_NUM);
	}
	
	@SuppressWarnings("rawtypes")
	public IPropertyValueSendable getSendable( String key ) {
		return mapSendableProperties.get(key);
	}

	public DefaultLinkState getState() {
		return delegate;
	}
	
	public void setBackground( FunctionWithException<Integer,DisplayStack,GetOperationFailedException> background ) {
		getState().setBackground(background);
	}
	
	public void setSliceNum(int sliceNum) {
		getState().setSliceNum(sliceNum);
	}
	
	/** Provides a copy of the current state (if you change it, it won't affect the global defaults ) */
	public DefaultLinkState copy() {
		return delegate.duplicate();
	}
	
	/** Provides a copy of the default module state with a changed background */
	public DefaultLinkState copyChangeBackground( FunctionWithException<Integer,DisplayStack,GetOperationFailedException> background ) {
		DefaultLinkState dup = delegate.duplicate();
		dup.setBackground(background);
		return dup;
	}
	
	private <T> void putMap( String id, IPropertyValueSendable<T> sendable ) {
		mapSendableProperties.put(id, sendable);
	}
}
