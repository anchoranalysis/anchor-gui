/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;

public abstract class FeatureListSrc {

    public abstract FeatureListWithRegionMap<FeatureInputSingleMemo> createInd();

    public abstract FeatureListWithRegionMap<FeatureInputPairMemo> createPair();

    public abstract FeatureListWithRegionMap<FeatureInputAllMemo> createAll();

    public abstract SharedFeatureMulti sharedFeatures();

    public abstract NRGStackWithParams maybeAugmentParams(NRGStackWithParams in)
            throws OperationFailedException;
}
