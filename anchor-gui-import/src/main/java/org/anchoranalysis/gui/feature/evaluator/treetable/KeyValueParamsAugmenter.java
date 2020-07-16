/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.KeyValueParamsForImageCreator;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;

public class KeyValueParamsAugmenter {

    private NRGScheme scheme;
    private SharedFeatureMulti sharedFeatures;
    private Logger logger;

    public KeyValueParamsAugmenter(
            NRGScheme scheme, SharedFeatureMulti sharedFeatures, Logger logger) {
        super();
        this.scheme = scheme;
        this.sharedFeatures = sharedFeatures;
        this.logger = logger;
    }

    public NRGStackWithParams augmentParams(NRGStackWithParams in) throws OperationFailedException {

        // We should add any image-params to the key value pairs
        KeyValueParamsForImageCreator creator =
                new KeyValueParamsForImageCreator(scheme, sharedFeatures, logger);
        try {
            KeyValueParams kpvNew = creator.createParamsForImage(in.getNrgStack());

            return addParams(in, kpvNew);

        } catch (FeatureCalcException e) {
            throw new OperationFailedException(e);
        }
    }

    private static NRGStackWithParams addParams(NRGStackWithParams in, KeyValueParams toAdd)
            throws OperationFailedException {

        KeyValueParams dup = in.getParams().duplicate();
        dup.putAll(toAdd);

        return in.copyChangeParams(dup);
    }
}
