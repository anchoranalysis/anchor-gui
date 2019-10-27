package org.anchoranalysis.gui.feature.evaluator.params;

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
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsDescriptor;
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsWithImageParamsDescriptor;
import org.anchoranalysis.feature.params.FeatureParamsDescriptor;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureResultsVectorCollectionParamsDescriptor;
import org.anchoranalysis.image.feature.histogram.FeatureHistogramParamsDescriptor;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParamsDescriptor;
import org.anchoranalysis.image.feature.objmask.collection.FeatureObjMaskCollectionDescriptor;
import org.anchoranalysis.image.feature.objmask.pair.FeatureObjMaskPairParamsDescriptor;
import org.anchoranalysis.image.feature.objmask.pair.merged.FeatureObjMaskPairMergedParamsDescriptor;
import org.anchoranalysis.image.feature.objmask.shared.FeatureObjMaskSharedObjectsParamsDescriptor;
import org.anchoranalysis.image.feature.pixelwise.score.PixelScoreFeatureCalcParamsDescriptor;
import org.anchoranalysis.image.feature.stack.nrg.FeatureNRGStackParamsDescriptor;

import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.nrg.NRGElemAllCalcParamsDescriptor;
import ch.ethz.biol.cell.mpp.nrg.NRGElemIndCalcParamsDescriptor;
import ch.ethz.biol.cell.mpp.nrg.NRGElemPairCalcParamsDescriptor;
import ch.ethz.biol.cell.mpp.nrg.feature.cfg.FeatureCfgParamsDescriptor;
import ch.ethz.biol.cell.mpp.nrg.feature.mark.FeatureMarkParamsDescriptor;

public class ParamsFactoryForFeature {

	public static FeatureCalcParamsFactory factoryFor( Feature f ) throws FeatureCalcException {
		
		FeatureParamsDescriptor paramType = f.paramType();
		
		if (paramType.equals(FeatureCalcParamsDescriptor.instance)) {
			return new NullParamsFactory();
		}
		
		if (paramType.equals(FeatureMarkParamsDescriptor.instance)) {
			return new FeatureMarkParamsFactory();
		}

		if (paramType.equals(FeatureObjMaskParamsDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}
		
		if (paramType.equals(NRGElemIndCalcParamsDescriptor.instance)) {
			return new NRGElemIndCalcParamsFactory();
		}
		
		if (paramType.equals(NRGElemPairCalcParamsDescriptor.instance)) {
			return new NRGElemPairCalcParamsFactory();
		}
		
		if (paramType.equals(NRGElemAllCalcParamsDescriptor.instance)) {
			return new NRGElemAllCalcParamsFactory();
		}
		
		if (paramType.equals(FeatureCfgParamsDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureObjMaskCollectionDescriptor.instance)) {
			return new FeatureObjMaskCollectionParamsFactory( RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE) );
		}
		
		if (paramType.equals(PixelScoreFeatureCalcParamsDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureObjMaskPairParamsDescriptor.instance)) {
			return new FeatureObjMaskPairParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}		
		
		if (paramType.equals(FeatureObjMaskPairMergedParamsDescriptor.instance)) {
			return new FeatureObjMaskPairMergedParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}		
		
		if (paramType.equals(FeatureHistogramParamsDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureResultsVectorCollectionParamsDescriptor.instance)) {
			return new UnsupportedFactory();
		}
		
		if (paramType.equals(FeatureNRGStackParamsDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}
		
		if (paramType.equals(FeatureCalcParamsWithImageParamsDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}

		if (paramType.equals(FeatureObjMaskSharedObjectsParamsDescriptor.instance)) {
			return new FeatureObjMaskParamsFactory( GlobalRegionIdentifiers.SUBMARK_INSIDE );
		}
		
		assert false;
		return new NullParamsFactory();
	}
}
