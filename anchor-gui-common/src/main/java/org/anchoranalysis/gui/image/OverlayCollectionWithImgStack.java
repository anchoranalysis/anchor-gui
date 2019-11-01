package org.anchoranalysis.gui.image;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class OverlayCollectionWithImgStack {

	private OverlayCollection overlayCollection;
	private NRGStackWithParams stack;
	
	public OverlayCollectionWithImgStack(OverlayCollection overlayCollection, NRGStackWithParams stack) {
		super();
		this.overlayCollection = overlayCollection;
		this.stack = stack;
	}

	public OverlayCollection getOverlayCollection() {
		return overlayCollection;
	}

	
	public NRGStackWithParams getStack() {
		return stack;
	}
	public void setStack(NRGStackWithParams stack) {
		this.stack = stack;
	}

	public void setOverlayCollection(OverlayCollection overlayCollection) {
		this.overlayCollection = overlayCollection;
	}
	
	public OverlayCollectionWithImgStack copyChangeStack( NRGStackWithParams stack ) {
		return new OverlayCollectionWithImgStack(overlayCollection, stack);
	}
}
