package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.bean.cfg.FeatureInputCfgDescriptor;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.FeatureInputMarkDescriptor;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemoDescriptor;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemoDescriptor;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemoDescriptor;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;

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

import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.input.descriptor.FeatureInputDescriptor;
import org.anchoranalysis.feature.input.descriptor.FeatureInputGenericDescriptor;
import org.anchoranalysis.feature.input.descriptor.FeatureInputParamsDescriptor;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResultsDescriptor;
import org.anchoranalysis.image.feature.histogram.FeatureInputHistogramDescriptor;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObjDescriptor;
import org.anchoranalysis.image.feature.objmask.collection.FeatureInputObjsDescriptor;
import org.anchoranalysis.image.feature.objmask.pair.FeatureInputPairObjsDescriptor;
import org.anchoranalysis.image.feature.objmask.pair.merged.FeatureInputPairObjsMergedDescriptor;
import org.anchoranalysis.image.feature.stack.nrg.FeatureInputNRGStackDescriptor;

public class ParamsFactoryForFeature {

	public static FeatureCalcParamsFactory factoryFor( Feature<?> f ) throws FeatureCalcException {
		
		FeatureInputDescriptor paramType = f.paramType();
		
		if (paramType.equals(FeatureInputGenericDescriptor.instance)) {
			return new NullParamsFactory();
		}
		
		if (paramType.equals(FeatureInputMarkDescriptor.instance)) {
			return new FeatureMarkParamsFactory();
		}

		if (paramType.equals(FeatureInputSingleObjDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}
		
		if (paramType.equals(FeatureInputSingleMemoDescriptor.instance)) {
			return new NRGElemIndCalcParamsFactory();
		}
		
		if (paramType.equals(FeatureInputPairMemoDescriptor.instance)) {
			return new NRGElemPairCalcParamsFactory();
		}
		
		if (paramType.equals(FeatureInputAllMemoDescriptor.instance)) {
			return new NRGElemAllCalcParamsFactory();
		}
		
		if (paramType.equals(FeatureInputCfgDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureInputObjsDescriptor.instance)) {
			return new FeatureObjMaskCollectionParamsFactory( RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE) );
		}
		
		if (paramType.equals(FeatureInputPairObjsDescriptor.instance)) {
			return new FeatureObjMaskPairParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}		
		
		if (paramType.equals(FeatureInputPairObjsMergedDescriptor.instance)) {
			return new FeatureObjMaskPairMergedParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}		
		
		if (paramType.equals(FeatureInputHistogramDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureInputResultsDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureInputNRGStackDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}
		
		if (paramType.equals(FeatureInputParamsDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}
		
		assert false;
		return new NullParamsFactory();
	}
}
