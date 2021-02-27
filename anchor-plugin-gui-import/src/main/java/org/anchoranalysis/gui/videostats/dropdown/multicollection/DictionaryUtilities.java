/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.dropdown.multicollection;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.value.Dictionary;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DictionaryUtilities {

    public static Dictionary apply(
            NamedProviderStore<Dictionary> paramsCollection, ErrorReporter errorReporter) {
        if (paramsCollection.keys().size() > 1) {

            // TODO change
            // HARDCORE a default for when we have more than one
            try {
                Optional<Dictionary> params = paramsCollection.getOptional("input_params");
                if (params.isPresent()) {
                    return params.get();
                }
            } catch (NamedProviderGetException e) {
                errorReporter.recordError(DictionaryUtilities.class, e.summarize());
            }

            errorReporter.recordError(
                    DictionaryUtilities.class,
                    new OperationFailedException(
                            "More than one params. Cannot choose between them."));
            return new Dictionary();
        } else if (paramsCollection.keys().size() == 1) {
            try {
                return paramsCollection.getException(paramsCollection.keys().iterator().next());
            } catch (NamedProviderGetException e) {
                errorReporter.recordError(DictionaryUtilities.class, e.summarize());
                return new Dictionary();
            }
        } else {
            return new Dictionary();
        }
    }
}
