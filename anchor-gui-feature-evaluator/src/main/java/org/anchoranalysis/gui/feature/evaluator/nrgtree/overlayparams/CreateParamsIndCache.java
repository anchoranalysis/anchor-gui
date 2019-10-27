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

import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateParamsIndFromRasterMark;

import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemoFactory;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMap;

class CreateParamsIndCache {
	
	private Mark mark;
	private NRGStackWithParams raster;
	private Map<RegionMap,CreateParams> map = new HashMap<RegionMap,CreateParams>();
	
	public CreateParamsIndCache(Mark mark, NRGStackWithParams raster) {
		super();
		this.mark = mark;
		this.raster = raster;
	}
	
	public CreateParams getOrCreate( RegionMap regionMap ) {
		
		CreateParams params = map.get(regionMap);
		
		if (params!=null) {
			return params;
		}
		
		PxlMarkMemo pmm = PxlMarkMemoFactory.create( mark, raster.getNrgStack(), regionMap );
		assert(pmm!=null);
		return new CreateParamsIndFromRasterMark(pmm,raster);
	}
}