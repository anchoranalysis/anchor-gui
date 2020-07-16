/* (C)2020 */
package org.anchoranalysis.gui.finder;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;

public class OperationFindNrgStackFromStackCollection
        extends CachedOperation<NRGStackWithParams, OperationFailedException>
        implements OperationWithProgressReporter<NRGStackWithParams, OperationFailedException> {

    // We first retrieve a namedimgcollection which we use to contstruct our real NrgStack for
    // purposes
    //   of good caching
    private OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            operationStackCollection;

    // An operation to retrieve a stackCollection
    //
    public OperationFindNrgStackFromStackCollection(
            OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
                    operationStackCollection) {
        super();
        this.operationStackCollection = operationStackCollection;
    }

    public OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            getOperationStackCollection() {
        return operationStackCollection;
    }

    @Override
    public NRGStackWithParams doOperation(ProgressReporter progressReporter)
            throws OperationFailedException {
        return doOperation();
    }

    @Override
    protected NRGStackWithParams execute() throws OperationFailedException {
        return createNRGStack();
    }

    // NB Note assumption about namedImgStackCollection ordering
    private NRGStackWithParams createNRGStack() throws OperationFailedException {

        NamedProvider<Stack> nic = operationStackCollection.doOperation(ProgressReporterNull.get());

        // We expects the keys to be the indexes
        Stack stack = populateStack(nic);

        if (stack.getNumChnl() > 0) {
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
                stack.addChnl(chnlFromStack(nic, c));
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

            if (chnlStack.getNumChnl() != 1) {
                throw new OperationFailedException("Stack should have only a single channel");
            }

            return chnlStack.getChnl(0);
        } catch (NamedProviderGetException e) {
            throw new OperationFailedException(e);
        }
    }
}
