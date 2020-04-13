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


import java.util.List;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayMark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.objmask.OverlayObjMask;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.FeatureWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateParamsIndFromObjMask;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateParamsPairFromObjMask;
import org.anchoranalysis.image.objmask.ObjMask;

public class CreateParamsFromOverlay {

	public static void addForOverlay(
		Overlay overlay,
		NRGStackWithParams nrgStack,
		FeatureListWithRegionMap<?> featureList,
		List<CreateParams<FeatureCalcParams>> listOut
	) {
		// TODO replace with object oriented-code
		if (overlay instanceof OverlayMark) {
			OverlayMark overlayCast = (OverlayMark) overlay;
			
			CreateParamsIndCache cache = new CreateParamsIndCache( overlayCast.getMark(), nrgStack );
			
			// Create a pair for each region map
			 
			for( FeatureWithRegionMap<?> f : featureList) {
				listOut.add( cache.getOrCreate(f.getRegionMap()) );
			}
		} else if (overlay instanceof OverlayObjMask) {
			
			OverlayObjMask overlayCast = (OverlayObjMask) overlay;
			
			CreateParamsIndFromObjMask createParams = new CreateParamsIndFromObjMask(overlayCast.getObjMask().getMask(), nrgStack);
			
			// We add the same createParams for each item in the feature list
			for( int i=0; i<featureList.size(); i++) {
				listOut.add( createParams );
			}
			
		} else {
			assert false;
		}
	}
	
	
	public static void addForOverlayPair(
		Pair<Overlay> pair,
		NRGStackWithParams raster,
		FeatureListWithRegionMap<?> featureList,
		List<CreateParams<FeatureCalcParams>> listOut
	) {
		if (pair.getSource() instanceof OverlayMark) {
			assert(pair.getDestination() instanceof OverlayMark);
			
			Mark source = ((OverlayMark) pair.getSource()).getMark();
			Mark dest = ((OverlayMark) pair.getDestination()).getMark();
			
			CreateParamsPairCache cache = new CreateParamsPairCache(
				source,
				dest,
				raster
			);
			
			for( FeatureWithRegionMap<?> f : featureList) {
				listOut.add( cache.getOrCreate(f.getRegionMap()) );
			}
			
		} else if (pair.getSource() instanceof OverlayObjMask ) {
			
			ObjMask source = ((OverlayObjMask) pair.getSource()).getObjMask().getMask();
			ObjMask dest = ((OverlayObjMask) pair.getDestination()).getObjMask().getMask();
			
			CreateParamsPairFromObjMask createParams = new CreateParamsPairFromObjMask(source,dest,raster);
			
			for( int i=0; i<featureList.size(); i++) {
				listOut.add( createParams );
			}
			
		}
		
	}
}
