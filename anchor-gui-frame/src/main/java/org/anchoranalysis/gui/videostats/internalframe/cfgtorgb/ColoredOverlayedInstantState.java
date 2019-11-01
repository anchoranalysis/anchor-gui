package org.anchoranalysis.gui.videostats.internalframe.cfgtorgb;

import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.index.SingleIndexCntr;

public class ColoredOverlayedInstantState extends SingleIndexCntr {

	private ColoredOverlayCollection coloredOverlayCollection;
	
	public ColoredOverlayedInstantState(int iter, ColoredOverlayCollection coloredOverlayCollection) {
		super(iter);
		this.coloredOverlayCollection = coloredOverlayCollection;
	}

	public ColoredOverlayCollection getOverlayCollection() {
		return coloredOverlayCollection;
	}
}
