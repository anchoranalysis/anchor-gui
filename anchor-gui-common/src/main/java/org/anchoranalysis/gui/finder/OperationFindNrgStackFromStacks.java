/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.finder;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CallableWithException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.CallableWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OperationFindNrgStackFromStacks
        implements CallableWithException<NRGStackWithParams, OperationFailedException> {

    /** We first retrieve a namedimgcollection which we use to construct our real NrgStack for purposes of good caching */
    private CallableWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            operationStackCollection;

    @Override
    public NRGStackWithParams call() throws OperationFailedException {
        // NB Note assumption about named-stack ordering
        NamedProvider<Stack> nic = operationStackCollection.call(ProgressReporterNull.get());

        // We expects the keys to be the indexes
        Stack stack = populateStack(nic);

        if (stack.getNumberChannels() > 0) {
            return new NRGStackWithParams(stack);
        } else {
            return null;
        }
    }

    private static Stack populateStack(NamedProvider<Stack> nic) throws OperationFailedException {

        Stack stack = new Stack();

        int size = nic.keys().size();
        for (int c = 0; c < size; c++) {

            try {
                stack.addChannel(chnlFromStack(nic, c));
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }

        return stack;
    }

    private static Channel chnlFromStack(NamedProvider<Stack> stackProvider, int c)
            throws OperationFailedException {

        try {
            Stack chnlStack = stackProvider.getException(Integer.toString(c));

            if (chnlStack.getNumberChannels() != 1) {
                throw new OperationFailedException("Stack should have only a single channel");
            }

            return chnlStack.getChannel(0);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e);
        }
    }
}
