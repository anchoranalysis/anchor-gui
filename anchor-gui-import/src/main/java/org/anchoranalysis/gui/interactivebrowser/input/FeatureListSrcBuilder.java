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

package org.anchoranalysis.gui.interactivebrowser.input;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.shared.FeaturesInitialization;
import org.anchoranalysis.gui.feature.evaluator.params.FeatureInputFactory;
import org.anchoranalysis.gui.feature.evaluator.params.ParamsFactoryForFeature;
import org.anchoranalysis.gui.feature.evaluator.treetable.DictionaryAugmenter;
import org.anchoranalysis.gui.feature.evaluator.treetable.ExtractFromEnergySchemeSet;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.feature.addcriteria.BoundingBoxIntersection;
import org.anchoranalysis.mpp.feature.bean.energy.scheme.EnergySchemeCreator;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergySchemeSet;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.mpp.feature.input.memo.FeatureInputSingleMemo;

@AllArgsConstructor
public class FeatureListSrcBuilder {

    private Logger logger;

    public FeatureListSrc build(
            FeaturesInitialization soFeature, EnergySchemeCreator energySchemeCreator)
            throws CreateException {

        EnergySchemeSet energySchemeSet = new EnergySchemeSet(soFeature.getSharedFeatures());

        if (energySchemeCreator != null) {
            return buildWith(soFeature, energySchemeSet, energySchemeCreator);

        } else {
            return buildWithout(soFeature, energySchemeSet);
        }
    }

    /** Build WITHOUT an existing energyScheme */
    private FeatureListSrc buildWithout(
            FeaturesInitialization soFeature, EnergySchemeSet energySchemeSet) {
        addFromStore(energySchemeSet, soFeature.getFeatureListSet(), RegionMapSingleton.instance());
        return new ExtractFromEnergySchemeSet(energySchemeSet);
    }

    /** Build WITH an existing energyScheme */
    private FeatureListSrc buildWith(
            FeaturesInitialization soFeature,
            EnergySchemeSet energySchemeSet,
            EnergySchemeCreator energySchemeCreator)
            throws CreateException {

        EnergyScheme energyScheme = createEnergyScheme(energySchemeCreator, soFeature, logger);
        RegionMapFinder.addFromEnergyScheme(energySchemeSet, energyScheme);

        addFromStore(energySchemeSet, soFeature.getFeatureListSet(), energyScheme.getRegionMap());

        // We deliberately do not used the SharedFeatures as we wish to keep the Image Features
        // separate and prevent any of the features being initialized prematurely.
        DictionaryAugmenter augmenter =
                new DictionaryAugmenter(energyScheme, soFeature.getSharedFeatures(), logger);

        return new ExtractFromEnergySchemeSet(energySchemeSet, augmenter);
    }

    private EnergyScheme createEnergyScheme(
            EnergySchemeCreator energySchemeCreator,
            FeaturesInitialization soFeature,
            Logger logger)
            throws CreateException {

        try {
            energySchemeCreator.initRecursive(soFeature, logger);
        } catch (InitException e1) {
            throw new CreateException(e1);
        }
        return energySchemeCreator.create();
    }

    private void addFromStore(
            EnergySchemeSet energySchemeSet,
            NamedProviderStore<FeatureList<FeatureInput>> store,
            RegionMap regionMap) {

        // Add each feature-list to the scheme, separating into unary and pairwise terms
        for (String key : store.keys()) {
            try {
                FeatureList<FeatureInput> fl = store.getException(key);

                // Put this in there, to get rid of error. Unsure why. It should go in refactoring
                // when FeatureSessions are properly implemented
                // TODO resolve this error
                // fl.init( new FeatureInitialization(soFeature.getSharedFeatureSet(),
                // soFeature.getCachedCalculationList()) );

                // Determines which features belong in the Unary part of the EnergyScheme, and which
                // in the Pairwise part.
                FeatureList<FeatureInputSingleMemo> outUnary = FeatureListFactory.empty();
                FeatureList<FeatureInputPairMemo> outPairwise = FeatureListFactory.empty();
                determineUnaryPairwiseFeatures(fl, outUnary, outPairwise);

                energySchemeSet.add(
                        key,
                        new EnergyScheme(
                                outUnary,
                                outPairwise,
                                FeatureListFactory.empty(),
                                regionMap,
                                new BoundingBoxIntersection() // Arbitrarily chosen
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
