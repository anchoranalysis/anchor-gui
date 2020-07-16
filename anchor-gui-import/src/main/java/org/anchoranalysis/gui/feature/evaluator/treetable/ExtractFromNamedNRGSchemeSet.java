/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.gui.feature.FeatureListUtilities;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;

public class ExtractFromNamedNRGSchemeSet extends FeatureListSrc {

    private NamedNRGSchemeSet src;
    private KeyValueParamsAugmenter augmenter;

    public ExtractFromNamedNRGSchemeSet(NamedNRGSchemeSet src) {
        this(src, null);
    }

    public ExtractFromNamedNRGSchemeSet(NamedNRGSchemeSet src, KeyValueParamsAugmenter augmenter) {
        super();
        this.src = src;
        this.augmenter = augmenter;
    }

    @Override
    public FeatureListWithRegionMap<FeatureInputSingleMemo> createInd() {
        return FeatureListUtilities.createFeatureList(
                src, nrgScheme -> nrgScheme.getElemIndAsFeatureList(), true);
    }

    @Override
    public FeatureListWithRegionMap<FeatureInputPairMemo> createPair() {
        return FeatureListUtilities.createFeatureList(
                src, nrgScheme -> nrgScheme.getElemPairAsFeatureList(), true);
    }

    @Override
    public FeatureListWithRegionMap<FeatureInputAllMemo> createAll() {
        return FeatureListUtilities.createFeatureList(
                src, nrgScheme -> nrgScheme.getElemAllAsFeatureList(), true);
    }

    @Override
    public SharedFeatureMulti sharedFeatures() {
        return src.getSharedFeatures();
    }

    @Override
    public NRGStackWithParams maybeAugmentParams(NRGStackWithParams in)
            throws OperationFailedException {

        if (augmenter != null) {
            return augmenter.augmentParams(in);
        } else {
            // We don't augment anything if there's no image-features
            return in;
        }
    }
}
