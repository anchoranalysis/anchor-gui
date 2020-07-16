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

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.stack.Stack;

class OperationNrgStack extends CachedOperation<NRGStackWithParams, CreateException>
        implements OperationWithProgressReporter<NRGStackWithParams, CreateException> {

    // We first retrieve a NamedImgCollection which we use to construct our real NrgStack for
    // purposes
    //   of good caching
    private Operation<MPPInitParams, ? extends Throwable> operationProposerSharedObjects;
    private KeyValueParams params;
    // An operation to retrieve a stackCollection
    //
    public OperationNrgStack(
            Operation<MPPInitParams, ? extends Throwable> operationProposerSharedObjects,
            KeyValueParams params) {
        super();
        this.params = params;
        this.operationProposerSharedObjects = operationProposerSharedObjects;
    }

    @Override
    protected NRGStackWithParams execute() throws CreateException {
        return creatergStack();
    }

    // NB Note assumption about namedImgStackCollection ordering
    private NRGStackWithParams creatergStack() throws CreateException {

        // System.out.println("Start Creating NRG Stack");
        try {

            Stack stack = new Stack();

            MPPInitParams soMPP = operationProposerSharedObjects.doOperation();

            NamedProvider<Stack> nic = soMPP.getImage().getStackCollection();

            // We expects the keys to be the indexes
            {
                // We try to automatically detect which of the two cases it can be
                // Either an NRG_STACK direct in the StackCollection or split amongst multiple
                //   files (but only those should exist in the directory)
                if (nic.keys().contains(ImgStackIdentifiers.NRG_STACK)) {
                    stack = nic.getException(ImgStackIdentifiers.NRG_STACK);
                } else {
                    throw new CreateException("Cannot find NRG_STACK");
                }
            }

            if (stack.getNumChnl() > 0) {
                NRGStack nrgStack = new NRGStack(stack);
                return new NRGStackWithParams(nrgStack, params);
            } else {
                return null;
            }

        } catch (Throwable e) {
            throw new CreateException(e);
        }
    }

    @Override
    public NRGStackWithParams doOperation(ProgressReporter progressReporter)
            throws CreateException {
        return doOperation();
    }
}
