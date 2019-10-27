package org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams;

/*-
 * #%L
 * anchor-gui-feature-evaluator
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.gui.feature.evaluator.params.ParamsFactoryForFeature;

import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;

public class CreateParamsIndFromRasterMark extends CreateParams  {

	private PxlMarkMemo pmm;
	private NRGStackWithParams raster;
	
	public CreateParamsIndFromRasterMark(PxlMarkMemo pmm,
			NRGStackWithParams raster) {
		super();
		this.pmm = pmm;
		this.raster = raster;
	}

	@Override
	public FeatureCalcParams createForFeature(Feature feature) throws CreateException {
		try {
			return ParamsFactoryForFeature.factoryFor( feature ).create(pmm, raster);
		} catch (FeatureCalcException e) {
			throw new CreateException(e);
		}
	}

}
