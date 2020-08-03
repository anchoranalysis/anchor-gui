/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.interactivebrowser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.experiment.identifiers.StackIdentifiers;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class CreateNrgStackHelper {

    // We first retrieve a NamedImgCollection which we use to construct our real NrgStack for
    // purposes of good caching
    public static NRGStackWithParams create(CheckedSupplier<MPPInitParams, ? extends Throwable> operationProposerSharedObjects, KeyValueParams params) throws CreateException {
        try {
            Stack stack;

            MPPInitParams soMPP = operationProposerSharedObjects.get();

            NamedProvider<Stack> nic = soMPP.getImage().getStackCollection();

            // We expects the keys to be the indexes
            {
                // We try to automatically detect which of the two cases it can be
                // Either an NRG_STACK direct in the StackCollection or split amongst multiple
                //   files (but only those should exist in the directory)
                if (nic.keys().contains(StackIdentifiers.NRG_STACK)) {
                    stack = nic.getException(StackIdentifiers.NRG_STACK);
                } else {
                    throw new CreateException("Cannot find NRG_STACK");
                }
            }

            if (stack.getNumberChannels() > 0) {
                return new NRGStackWithParams(new NRGStack(stack), params);
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new CreateException(e);
        }
    }
}
