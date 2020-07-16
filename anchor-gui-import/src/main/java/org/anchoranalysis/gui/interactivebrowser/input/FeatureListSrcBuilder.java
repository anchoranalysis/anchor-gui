/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.input;

import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.BBoxIntersection;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.gui.feature.evaluator.params.FeatureInputFactory;
import org.anchoranalysis.gui.feature.evaluator.params.ParamsFactoryForFeature;
import org.anchoranalysis.gui.feature.evaluator.treetable.ExtractFromNamedNRGSchemeSet;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.feature.evaluator.treetable.KeyValueParamsAugmenter;

@AllArgsConstructor
public class FeatureListSrcBuilder {

    private Logger logger;

    public FeatureListSrc build(
            SharedFeaturesInitParams soFeature, NRGSchemeCreator nrgSchemeCreator)
            throws CreateException {

        NamedNRGSchemeSet nrgElemSet = new NamedNRGSchemeSet(soFeature.getSharedFeatureSet());

        if (nrgSchemeCreator != null) {
            return buildWith(soFeature, nrgElemSet, nrgSchemeCreator);

        } else {
            return buildWithout(soFeature, nrgElemSet);
        }
    }

    /** Build WITHOUT an existing nrgScheme */
    private FeatureListSrc buildWithout(
            SharedFeaturesInitParams soFeature, NamedNRGSchemeSet nrgElemSet) {
        addFromStore(nrgElemSet, soFeature.getFeatureListSet(), RegionMapSingleton.instance());
        return new ExtractFromNamedNRGSchemeSet(nrgElemSet);
    }

    /** Build WITH an existing nrgScheme */
    private FeatureListSrc buildWith(
            SharedFeaturesInitParams soFeature,
            NamedNRGSchemeSet nrgElemSet,
            NRGSchemeCreator nrgSchemeCreator)
            throws CreateException {

        NRGScheme nrgScheme = createNRGScheme(nrgSchemeCreator, soFeature, logger);
        RegionMapFinder.addFromNrgScheme(nrgElemSet, nrgScheme);

        addFromStore(nrgElemSet, soFeature.getFeatureListSet(), nrgScheme.getRegionMap());

        // We deliberately do not used the SharedFeatures as we wish to keep the Image Features
        // seperate
        //  and prevent any of the features being initialized prematurely
        KeyValueParamsAugmenter augmenter =
                new KeyValueParamsAugmenter(nrgScheme, soFeature.getSharedFeatureSet(), logger);

        return new ExtractFromNamedNRGSchemeSet(nrgElemSet, augmenter);
    }

    private NRGScheme createNRGScheme(
            NRGSchemeCreator nrgSchemeCreator, SharedFeaturesInitParams soFeature, Logger logger)
            throws CreateException {

        try {
            nrgSchemeCreator.initRecursive(soFeature, logger);
        } catch (InitException e1) {
            throw new CreateException(e1);
        }
        return nrgSchemeCreator.create();
    }

    private void addFromStore(
            NamedNRGSchemeSet nrgElemSet,
            NamedProviderStore<FeatureList<FeatureInput>> store,
            RegionMap regionMap) {

        // Add each feature-list to the scheme, separating into unary and pairwise terms
        for (String key : store.keys()) {
            try {
                FeatureList<FeatureInput> fl = store.getException(key);

                // Put this in there, to get rid of error. Unsure why. It should go in refactoring
                // when FeatureSessions are properly implemented
                // TODO resolve this error
                // fl.init( new FeatureInitParams(soFeature.getSharedFeatureSet(),
                // soFeature.getCachedCalculationList()) );

                // Determines which features belong in the Unary part of the NRGScheme, and which in
                // the Pairwise part
                FeatureList<FeatureInputSingleMemo> outUnary = FeatureListFactory.empty();
                FeatureList<FeatureInputPairMemo> outPairwise = FeatureListFactory.empty();
                determineUnaryPairwiseFeatures(fl, outUnary, outPairwise);

                nrgElemSet.add(
                        key,
                        new NRGScheme(
                                outUnary,
                                outPairwise,
                                FeatureListFactory.empty(),
                                regionMap,
                                new BBoxIntersection() // Arbitrarily chosen
                                ));

            } catch (CreateException e) {
                logger.errorReporter().recordError(FeatureListSrcBuilder.class, e);
            } catch (NamedProviderGetException e) {
                logger.errorReporter().recordError(FeatureListSrcBuilder.class, e.summarize());
            }
        }
    }

    private void determineUnaryPairwiseFeatures(
            FeatureList<FeatureInput> in,
            FeatureList<FeatureInputSingleMemo> outUnary,
            FeatureList<FeatureInputPairMemo> outPairwise) {
        for (Feature<FeatureInput> feature : in) {

            FeatureInputFactory factory = ParamsFactoryForFeature.factoryFor(feature);

            if (factory.isUnarySupported()) {
                outUnary.add(feature.downcast());
            }

            if (factory.isPairwiseSupported()) {
                outPairwise.add(feature.downcast());
            }
        }
    }
}
