/*-
 * #%L
 * anchor-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.gui.feature.evaluator.treetable;

import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergyScheme;
import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergySchemeSet;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListUtilities;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;

@AllArgsConstructor
public class ExtractFromEnergySchemeSet extends FeatureListSrc {

    private EnergySchemeSet source;
    private KeyValueParamsAugmenter augmenter;

    public ExtractFromEnergySchemeSet(EnergySchemeSet src) {
        this(src, null);
    }

    @Override
    public FeatureListWithRegionMap<FeatureInputSingleMemo> createInd() {
        return FeatureListUtilities.createFeatureList(
                source, EnergyScheme::getElemIndAsFeatureList, true);
    }

    @Override
    public FeatureListWithRegionMap<FeatureInputPairMemo> createPair() {
        return FeatureListUtilities.createFeatureList(
                source, EnergyScheme::getElemPairAsFeatureList, true);
    }

    @Override
    public FeatureListWithRegionMap<FeatureInputAllMemo> createAll() {
        return FeatureListUtilities.createFeatureList(
                source, EnergyScheme::getElemAllAsFeatureList, true);
    }

    @Override
    public SharedFeatureMulti sharedFeatures() {
        return source.getSharedFeatures();
    }

    @Override
    public EnergyStack maybeAugmentParams(EnergyStack in) throws OperationFailedException {

        if (augmenter != null) {
            return augmenter.augmentParams(in);
        } else {
            // We don't augment anything if there's no image-features
            return in;
        }
    }
}
