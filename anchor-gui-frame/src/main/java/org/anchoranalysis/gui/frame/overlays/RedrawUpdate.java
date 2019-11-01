package org.anchoranalysis.gui.frame.overlays;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;

import ch.ethz.biol.cell.imageprocessing.io.generator.raster.OverlayedDisplayStackUpdate;

/** Constructors a redraw update from some underlying objects */
public class RedrawUpdate {

	private OverlayedDisplayStackUpdate update;
	private OverlayCollection overlaysForTrigger;
	private int suggestedSliceNum;	// Or -1 disables

	public RedrawUpdate(OverlayedDisplayStackUpdate update, OverlayCollection overlaysForTrigger,
			int suggestedSliceNum) {
		super();
		this.update = update;
		this.overlaysForTrigger = overlaysForTrigger;
		this.suggestedSliceNum = suggestedSliceNum;
	}

	public OverlayedDisplayStackUpdate getUpdate() {
		return update;
	}

	public OverlayCollection getOverlaysForTrigger() {
		return overlaysForTrigger;
	}

	public int getSuggestedSliceNum() {
		return suggestedSliceNum;
	}
}
