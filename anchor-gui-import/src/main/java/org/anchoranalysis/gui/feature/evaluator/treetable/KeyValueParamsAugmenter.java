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

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.KeyValueParamsForImageCreator;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalculationException;
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

        } catch (CreateException e) {
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
