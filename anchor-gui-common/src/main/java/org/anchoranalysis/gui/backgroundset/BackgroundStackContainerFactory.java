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

package org.anchoranalysis.gui.backgroundset;

import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.container.background.BackgroundStackContainer;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class BackgroundStackContainerFactory {

    private static DisplayStack convert(Stack s) throws CreateException {
        if (s.getNumberChannels() <= 3) {
            s = s.extractUpToThreeChannels();
        }
        return DisplayStack.create(s);
    }

    @AllArgsConstructor
    private static class ExistingStack implements BackgroundStackContainer {

        private final BoundedIndexContainer<DisplayStack> container;

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public BoundedIndexContainer<DisplayStack> container() {
            return container;
        }
    }

    public static BackgroundStackContainer convertedSequence(TimeSequence seq)
            throws OperationFailedException {

        BoundedIndexContainer<DisplayStack> bridge =
                new BoundedIndexContainerBridgeWithoutIndex<>(
                        new TimeSequenceBridge(seq),
                        s -> {
                            try {
                                return convert(s);
                            } catch (CreateException e) {
                                throw new BridgeElementException(e);
                            }
                        });

        return new ExistingStack(bridge);
    }

    public static BackgroundStackContainer singleSavedStack(Stack stack)
            throws OperationFailedException {

        try {
            final DisplayStack stackPadded = convert(stack);

            return new ExistingStack(new SingleContainer<>(stackPadded, 0, true));

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
