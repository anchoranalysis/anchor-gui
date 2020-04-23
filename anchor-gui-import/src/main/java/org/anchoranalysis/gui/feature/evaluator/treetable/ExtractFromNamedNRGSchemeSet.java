package org.anchoranalysis.gui.feature.evaluator.treetable;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;

/*-
 * #%L
 * anchor-gui-import
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
import org.anchoranalysis.feature.calc.params.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.gui.feature.FeatureListUtilities;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;

public class ExtractFromNamedNRGSchemeSet extends FeatureListSrc {

	private NamedNRGSchemeSet src;
	private KeyValueParamsAugmenter augmenter;

	public ExtractFromNamedNRGSchemeSet(NamedNRGSchemeSet src) {
		this(src, null);
	}
	
	/**
	 * 
	 * @param src
	 * @param augmenter adds the parent from which the src was derived. NULL if it doesn't exist
	 */
	public ExtractFromNamedNRGSchemeSet(NamedNRGSchemeSet src, KeyValueParamsAugmenter augmenter ) {
		super();
		this.src = src;
		this.augmenter = augmenter;
	}
		
	@Override
	public FeatureListWithRegionMap<FeatureInputSingleMemo> createInd() {
		return FeatureListUtilities.createFeatureList(
			src,
			nrgScheme -> nrgScheme.getElemIndAsFeatureList(),
			true
		);
	}

	@Override
	public FeatureListWithRegionMap<FeatureInputPairMemo> createPair() {
		return FeatureListUtilities.createFeatureList(
			src,
			nrgScheme -> nrgScheme.getElemPairAsFeatureList(),
			true
		);
	}

	@Override
	public FeatureListWithRegionMap<FeatureInputAllMemo> createAll() {
		return FeatureListUtilities.createFeatureList(
			src,
			nrgScheme -> nrgScheme.getElemAllAsFeatureList(),
			true
		);		
	}

	@Override
	public SharedFeatureSet<FeatureInputNRGStack> sharedFeatures() {
		return src.getSharedFeatures();
	}

	@Override
	public NRGStackWithParams maybeAugmentParams(NRGStackWithParams in) throws OperationFailedException {

		if (augmenter!=null) {
			return augmenter.augmentParams(in);
		} else {
			// We don't augment anything if there's no image-features
			return in;
		}
	}
}
