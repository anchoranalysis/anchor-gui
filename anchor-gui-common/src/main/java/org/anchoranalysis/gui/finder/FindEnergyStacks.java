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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.NamedStacksSupplier;
import org.anchoranalysis.image.core.stack.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindEnergyStacks {

    /**
     * We first retrieve a namedimgcollection which we use to construct our real EnergyStack for
     * purposes of good caching
     */
    public static EnergyStack find(NamedStacksSupplier stacks) throws OperationFailedException {
        // NB Note assumption about named-stack ordering
        NamedProvider<Stack> provider = stacks.get(ProgressReporterNull.get());

        // We expects the keys to be the indexes
        Stack stack = allChannelsInStack(provider);

        if (stack.getNumberChannels() > 0) {
            return new EnergyStack(stack);
        } else {
            return null;
        }
    }

    private static Stack allChannelsInStack(NamedProvider<Stack> namedStacks)
            throws OperationFailedException {

        int size = namedStacks.keys().size();

        Stack out = new Stack();
        for (int channelIndex = 0; channelIndex < size; channelIndex++) {

            try {
                out.addChannel(channelFromStack(namedStacks, channelIndex));
            } catch (IncorrectImageSizeException e) {
                throw new OperationFailedException(e);
            }
        }
        return out;
    }

    private static Channel channelFromStack(NamedProvider<Stack> stackProvider, int channelIndex)
            throws OperationFailedException {

        try {
            Stack channelStack = stackProvider.getException(Integer.toString(channelIndex));

            if (channelStack.getNumberChannels() != 1) {
                throw new OperationFailedException("Stack should have only a single channel");
            }

            return channelStack.getChannel(0);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e);
        }
    }
}
