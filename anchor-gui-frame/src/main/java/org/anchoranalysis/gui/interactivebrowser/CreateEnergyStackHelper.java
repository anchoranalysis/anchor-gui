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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.CheckedSupplier;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.energy.EnergyStackWithoutParams;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.experiment.identifiers.StackIdentifiers;
import org.anchoranalysis.mpp.bean.init.MPPInitParams;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CreateEnergyStackHelper {

    // We first retrieve a NamedImgCollection which we use to construct our real EnergyStack for
    // purposes of good caching
    public static EnergyStack create(
            CheckedSupplier<MPPInitParams, ? extends Throwable> operationProposerSharedObjects,
            KeyValueParams params)
            throws CreateException {
        try {
            MPPInitParams soMPP = operationProposerSharedObjects.get();

            // We expects the keys to be the indexes
            Stack stack = deriveEnergyStack(soMPP.getImage().stacks());

            if (stack.getNumberChannels() > 0) {
                return new EnergyStack(new EnergyStackWithoutParams(stack), params);
            } else {
                throw new CreateException("The stack has no channels");
            }

        } catch (Exception e) {
            throw new CreateException(e);
        }
    }

    private static Stack deriveEnergyStack(NamedProvider<Stack> nic) throws CreateException {
        // We try to automatically detect which of the two cases it can be
        // Either an ENERGY_STACK direct in the StackCollection or split amongst multiple
        //   files (but only those should exist in the directory)
        if (nic.keys().contains(StackIdentifiers.ENERGY_STACK)) {
            try {
                return nic.getException(StackIdentifiers.ENERGY_STACK);
            } catch (NamedProviderGetException e) {
                throw new CreateException(e.summarize());
            }
        } else {
            throw new CreateException("Cannot find ENERGY_STACK");
        }
    }
}
