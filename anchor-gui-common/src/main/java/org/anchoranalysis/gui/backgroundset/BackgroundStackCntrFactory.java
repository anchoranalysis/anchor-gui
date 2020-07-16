/* (C)2020 */
package org.anchoranalysis.gui.backgroundset;

import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;

public class BackgroundStackCntrFactory {

    private static DisplayStack convert(Stack s) throws CreateException {
        if (s.getNumChnl() <= 3) {
            s = s.extractUpToThreeChnls();
        }
        return DisplayStack.create(s);
    }

    private static class ExistingStack implements BackgroundStackCntr {

        private BoundedIndexContainer<DisplayStack> cntr;

        public ExistingStack(BoundedIndexContainer<DisplayStack> cntr) {
            super();
            this.cntr = cntr;
        }

        @Override
        public boolean exists() {
            return true;
        }

        @Override
        public BoundedIndexContainer<DisplayStack> backgroundStackCntr()
                throws GetOperationFailedException {
            // TODO Auto-generated method stub
            return cntr;
        }
    }

    public static BackgroundStackCntr convertedSequence(TimeSequence seq)
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

    public static BackgroundStackCntr singleSavedStack(Stack stack)
            throws OperationFailedException {

        try {
            final DisplayStack stackPadded = convert(stack);

            return new ExistingStack(new SingleContainer<>(stackPadded, 0, true));

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
