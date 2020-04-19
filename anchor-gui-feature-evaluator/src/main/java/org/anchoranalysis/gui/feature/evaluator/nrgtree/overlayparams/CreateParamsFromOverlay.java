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
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateParamsIndFromObjMask;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateParamsPairFromObjMask;
import org.anchoranalysis.image.objmask.ObjMask;

public class CreateParamsFromOverlay {

	public static CreateParams<FeatureCalcParams> addForOverlay(
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
			
			return new CreateParamsIndFromObjMask(
				overlayCast.getObjMask().getMask(),
				nrgStack
			);
			
		} else {
			assert false;
			return null;
		}
	}
	
	
	public static CreateParams<FeatureCalcParams> addForOverlayPair(
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
			
			ObjMask source = ((OverlayObjMask) pair.getSource()).getObjMask().getMask();
			ObjMask dest = ((OverlayObjMask) pair.getDestination()).getObjMask().getMask();
			
			return new CreateParamsPairFromObjMask(
				source,
				dest,
				raster
			);
		} else {
			return null;
		}
	}
}
