package org.anchoranalysis.gui.videostats.link;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.functional.FunctionWithException;
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
