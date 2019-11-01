package org.anchoranalysis.gui.feature.evaluator.singlepair;

import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public interface IUpdatableSinglePair {
	
	void updateSingle( final Overlay overlay, NRGStackWithParams raster );
	
	void updatePair( final Pair<Overlay> pair, NRGStackWithParams raster);
}
