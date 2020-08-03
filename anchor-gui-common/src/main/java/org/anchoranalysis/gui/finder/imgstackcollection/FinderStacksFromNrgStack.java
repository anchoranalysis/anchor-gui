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

package org.anchoranalysis.gui.finder.imgstackcollection;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.store.StoreSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.image.stack.NamedStacksSupplier;
import org.anchoranalysis.image.stack.NamedStacksSet;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FinderStacksFromNrgStack implements FinderStacks {

    // START REQUIRED ARGUMENTS
    private final FinderNrgStack delegate;
    // END REQUIRED ARGUMENTS

    private NamedStacksSupplier
            operationStacks = NamedStacksSupplier.cache(this::buildStacks);
        
    @Override
    public NamedProvider<Stack> getStacks() throws OperationFailedException {
        return operationStacks.get(ProgressReporterNull.get());
    }

    @Override
    public NamedStacksSupplier
            getStacksAsOperation() {
        return operationStacks;
    }

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {
        return delegate.doFind(manifestRecorder);
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }
    
    private NamedProvider<Stack> buildStacks(ProgressReporter progressReporter) throws OperationFailedException {
        NamedStacksSet stackCollection = new NamedStacksSet();

        // finder NRG stack
        if (delegate != null && delegate.exists()) {

            // Should we mention when we only have the first 3?
            stackCollection.addImageStack(
                    "nrgStack", StoreSupplier.cache( this::extractStack ));

            stackCollection.addFromWithPrefix(
                    delegate.getNamedStacks(), "nrgChnl-");
        }
        return stackCollection; 
    }

    private Stack extractStack() throws OperationFailedException {
        try {
            NRGStackWithParams nrgStackWithParams =
                    delegate.operationNrgStackWithProgressReporter()
                            .get(ProgressReporterNull.get());
            return nrgStackWithParams
                    .getNrgStack()
                    .asStack()
                    .extractUpToThreeChannels();
        } catch (GetOperationFailedException e) {
            throw e.asOperationFailedException();
        }
    }
}
