/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator.params;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputEnergy;
import org.anchoranalysis.feature.input.FeatureInputParams;
import org.anchoranalysis.feature.input.FeatureInputResults;
import org.anchoranalysis.image.feature.input.FeatureInputHistogram;
import org.anchoranalysis.image.feature.input.FeatureInputObjectCollection;
import org.anchoranalysis.image.feature.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.input.FeatureInputSingleObject;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.feature.bean.mark.FeatureInputMark;
import org.anchoranalysis.mpp.feature.bean.mark.collection.FeatureInputMarkCollection;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParamsFactoryForFeature {

    public static FeatureInputFactory factoryFor(Feature<?> f) {

        Class<? extends FeatureInput> paramType = f.inputType();

        if (paramType.equals(FeatureInput.class)) {
            return new NullFactory();
        }

        if (paramType.equals(FeatureInputMark.class)) {
            return new MarkUnaryFactory();
        }

        if (paramType.equals(FeatureInputSingleObject.class)) {
            return new ObjectUnaryFactory(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        }

        if (paramType.equals(FeatureInputSingleMemo.class)) {
            return new MemoUnaryFactory();
        }

        if (paramType.equals(FeatureInputPairMemo.class)) {
            return new MemoPairwiseFactory();
        }

        if (paramType.equals(FeatureInputAllMemo.class)) {
            return new EnergyAllInputsFactory();
        }

        if (paramType.equals(FeatureInputMarkCollection.class)) {
            return new UnsupportedFactory();
        }

        if (paramType.equals(FeatureInputObjectCollection.class)) {
            return new ObjectCollectionUnaryFactory(
                    RegionMapSingleton.instance()
                            .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE));
        }

        if (paramType.equals(FeatureInputPairObjects.class)) {
            return new ObjectPairwiseFactory(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        }

        if (paramType.equals(FeatureInputHistogram.class)) {
            return new UnsupportedFactory();
        }

        if (paramType.equals(FeatureInputResults.class)) {
            return new UnsupportedFactory();
        }

        if (paramType.equals(FeatureInputEnergy.class)) {
            return new ObjectUnaryFactory(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        }

        if (paramType.equals(FeatureInputParams.class)) {
            return new ObjectUnaryFactory(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        }

        assert false;
        return new NullFactory();
    }
}
