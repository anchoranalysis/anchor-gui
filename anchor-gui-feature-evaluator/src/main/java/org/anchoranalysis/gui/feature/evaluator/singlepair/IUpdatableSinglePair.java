package org.anchoranalysis.gui.feature.evaluator.singlepair;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

import ch.ethz.biol.cell.mpp.pair.Pair;

public interface IUpdatableSinglePair {
	
	void updateSingle( final Overlay overlay, NRGStackWithParams raster );
	
	void updatePair( final Pair<Overlay> pair, NRGStackWithParams raster);
}
