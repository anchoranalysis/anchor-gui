package org.anchoranalysis.gui.feature.evaluator.nrgtree.overlayparams;

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


import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayMark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.objmask.OverlayObjMask;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateIndFromObj;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreatePairFromObj;
import org.anchoranalysis.image.objectmask.ObjectMask;

public class CreateParamsFromOverlay {

	public static CreateFeatureInput<FeatureInput> addForOverlay(
		Overlay overlay,
		NRGStackWithParams nrgStack,
		FeatureListWithRegionMap<?> featureList
	) {
		// TODO replace with object oriented-code
		if (overlay instanceof OverlayMark) {
			OverlayMark overlayCast = (OverlayMark) overlay;
			
			CreateParamsInd cache = new CreateParamsInd( overlayCast.getMark(), nrgStack );
			return cache.getOrCreate(
				featureList.get(0).getRegionMap()
			);

		} else if (overlay instanceof OverlayObjMask) {
			
			OverlayObjMask overlayCast = (OverlayObjMask) overlay;
			
			return new CreateIndFromObj(
				overlayCast.getObjMask().getMask(),
				nrgStack
			);
			
		} else {
			assert false;
			return null;
		}
	}
	
	
	public static CreateFeatureInput<FeatureInput> addForOverlayPair(
		Pair<Overlay> pair,
		NRGStackWithParams raster,
		FeatureListWithRegionMap<?> featureList
	) {
		if (pair.getSource() instanceof OverlayMark) {
			assert(pair.getDestination() instanceof OverlayMark);
			
			Mark source = ((OverlayMark) pair.getSource()).getMark();
			Mark dest = ((OverlayMark) pair.getDestination()).getMark();
			
			CreateParamsPair cache = new CreateParamsPair(
				source,
				dest,
				raster
			);
			return cache.getOrCreate(featureList.get(0).getRegionMap());
			
		} else if (pair.getSource() instanceof OverlayObjMask ) {
			
			ObjectMask source = ((OverlayObjMask) pair.getSource()).getObjMask().getMask();
			ObjectMask dest = ((OverlayObjMask) pair.getDestination()).getObjMask().getMask();
			
			return new CreatePairFromObj(
				source,
				dest,
				raster
			);
		} else {
			return null;
		}
	}
}
