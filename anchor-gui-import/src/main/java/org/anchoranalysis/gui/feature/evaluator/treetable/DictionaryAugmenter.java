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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.value.Dictionary;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.mpp.feature.energy.scheme.DictionaryForImageCreator;
import org.anchoranalysis.mpp.feature.energy.scheme.EnergyScheme;

@AllArgsConstructor
public class DictionaryAugmenter {

    private EnergyScheme scheme;
    private SharedFeatureMulti sharedFeatures;
    private Logger logger;

    public EnergyStack augmentParams(EnergyStack in) throws OperationFailedException {

        // We should add any image-params to the key value pairs
        DictionaryForImageCreator creator =
                new DictionaryForImageCreator(scheme, sharedFeatures, logger);
        try {
            return addDictionary(in, creator.create(in.withoutParams()));

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    private static EnergyStack addDictionary(EnergyStack in, Dictionary toAdd)
            throws OperationFailedException {

        Dictionary dup = in.getDictionary().duplicate();
        dup.putCheck(toAdd);

        return in.copyChangeParams(dup);
    }
}
