/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.feature.bean.cfg.FeatureInputCfg;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.FeatureInputMark;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.input.FeatureInputParams;
import org.anchoranalysis.feature.resultsvectorcollection.FeatureInputResults;
import org.anchoranalysis.image.feature.histogram.FeatureInputHistogram;
import org.anchoranalysis.image.feature.object.input.FeatureInputObjectCollection;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;

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
            return new NRGElemAllCalcParamsFactory();
        }

        if (paramType.equals(FeatureInputCfg.class)) {
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

        if (paramType.equals(FeatureInputNRG.class)) {
            return new ObjectUnaryFactory(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        }

        if (paramType.equals(FeatureInputParams.class)) {
            return new ObjectUnaryFactory(GlobalRegionIdentifiers.SUBMARK_INSIDE);
        }

        assert false;
        return new NullFactory();
    }
}
