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


import java.util.HashMap;
import java.util.Map;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateParamsPairFromRasterMark;

class CreateParamsPair {
	
	private Mark markSrc;
	private Mark markDest;
	private NRGStackWithParams raster;
	private Map<RegionMap,CreateParams<FeatureCalcParams>> map = new HashMap<>();
	
	public CreateParamsPair(Mark markSrc, Mark markDest, NRGStackWithParams raster) {
		super();
		this.markSrc = markSrc;
		this.markDest = markDest;
		this.raster = raster;
	}
	
	public CreateParams<FeatureCalcParams> getOrCreate( RegionMap regionMap ) {
		
		CreateParams<FeatureCalcParams> params = map.get(regionMap);
		
		if (params!=null) {
			return params;
		}
		
		PxlMarkMemo pmmSrc = PxlMarkMemoFactory.create( markSrc, raster.getNrgStack(), regionMap );
		PxlMarkMemo pmmDest = PxlMarkMemoFactory.create( markDest, raster.getNrgStack(), regionMap );
		params = new CreateParamsPairFromRasterMark( pmmSrc, pmmDest, raster);
		return params;
	}
}