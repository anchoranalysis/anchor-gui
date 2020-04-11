package org.anchoranalysis.gui.feature.evaluator.treetable;

import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemAllCalcParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemIndCalcParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParams;

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

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParamsNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;

public abstract class FeatureListSrc {

	public abstract FeatureListWithRegionMap<NRGElemIndCalcParams> createInd();
	
	public abstract FeatureListWithRegionMap<NRGElemPairCalcParams> createPair();
	
	public abstract FeatureListWithRegionMap<NRGElemAllCalcParams> createAll();
	
	public abstract SharedFeatureSet<FeatureCalcParamsNRGStack> sharedFeatures();
	
	/**
	 * Maybe adds additional KeyValueParams to a stack
	 * 
	 * Note that if the params are changed, the input-stack should first be copied
	 *  so to keep the input-object unchanged
	 * 
	 * If params are unchanged, it's safe to return the same object as is passed in
	 * 
	 * @param in
	 * @return
	 */
	public abstract NRGStackWithParams maybeAugmentParams( NRGStackWithParams in ) throws OperationFailedException;
}
