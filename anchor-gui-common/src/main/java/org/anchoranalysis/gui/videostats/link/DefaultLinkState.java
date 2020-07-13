package org.anchoranalysis.gui.videostats.link;

/*-
 * #%L
 * anchor-gui-common
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;
import org.anchoranalysis.image.stack.DisplayStack;
import org.apache.commons.lang.ArrayUtils;

/** Default values for all the linked variables */
public class DefaultLinkState {
	
	private int frameIndex;
	private int sliceNum;
	private int[] objectIDs = new int[]{};
	private OverlayCollection overlayCollection;
	private OverlayCollectionWithImgStack cfgWithStack;
	private FunctionWithException<Integer,DisplayStack,GetOperationFailedException> background;
	
	DefaultLinkState duplicate() {
		DefaultLinkState dms = new DefaultLinkState();
		dms.frameIndex = frameIndex;
		dms.sliceNum = sliceNum;
		dms.objectIDs = ArrayUtils.clone( objectIDs );
		dms.overlayCollection = overlayCollection;
		dms.cfgWithStack = cfgWithStack;
		dms.background = background;
		return dms;
	}
	
	public int getSliceNum() {
		return sliceNum;
	}
	void setSliceNum(int sliceNum) {
		this.sliceNum = sliceNum;
	}
	public int[] getObjectIDs() {
		return objectIDs;
	}
	void setObjectIDs(int[] objectIDs) {
		this.objectIDs = objectIDs;
	}
	public int getFrameIndex() {
		return frameIndex;
	}
	
	void setFrameIndex(int frameIndex) {
		this.frameIndex = frameIndex;
	}
	public FunctionWithException<Integer,DisplayStack,GetOperationFailedException> getBackground() {
		return background;
	}
	
	void setBackground(
			FunctionWithException<Integer,DisplayStack,GetOperationFailedException> background) {
		this.background = background;
	}
	
	public OverlayCollectionWithImgStack getCfgWithStack() {
		return cfgWithStack;
	}
	
	void setCfgWithStack(OverlayCollectionWithImgStack cfgWithStack) {
		this.cfgWithStack = cfgWithStack;
	}

	public OverlayCollection getOverlayCollection() {
		return overlayCollection;
	}

	void setOverlayCollection(OverlayCollection overlayCollection) {
		this.overlayCollection = overlayCollection;
	}
}
