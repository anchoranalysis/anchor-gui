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

import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.finder.FinderNrgStack;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;

public class FinderImgStackCollectionFromNrgStack implements FinderImgStackCollection {

    private FinderNrgStack delegate = null;

    private OperationWithProgressReporter<Stack, OperationFailedException>
            operationExtractUntilThreeChnls =
                    new WrapOperationWithProgressReporterAsCached<>(
                            pr -> {
                                try {
                                    NRGStackWithParams nrgStackWithParams = delegate.operationNrgStackWithProgressReporter()
                                            .doOperation(pr);
                                    return nrgStackWithParams
                                            .getNrgStack()
                                            .asStack()
                                            .extractUpToThreeChannels();                                        
                                } catch (GetOperationFailedException e) {
                                    throw e.asOperationFailedException();
                                }
                            });

    private CachedOperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            operationImgStackCollection =
                    new WrapOperationWithProgressReporterAsCached<>(
                            pr -> {
                                NamedImgStackCollection stackCollection =
                                        new NamedImgStackCollection();

                                // finder NRG stack
                                if (delegate != null && delegate.exists()) {

                                    // Should we mention when we only have the first 3?
                                    stackCollection.addImageStack(
                                        "nrgStack",
                                        operationExtractUntilThreeChnls
                                    );

                                    stackCollection.addFromWithPrefix(
                                            delegate.getNamedImgStackCollection(), "nrgChnl-");
                                }
                                return stackCollection;
                            });

    public FinderImgStackCollectionFromNrgStack(FinderNrgStack finderNrgStack) {
        this.delegate = finderNrgStack;
    }

    @Override
    public NamedProvider<Stack> getImgStackCollection() throws OperationFailedException {
        return operationImgStackCollection.doOperation(ProgressReporterNull.get());
    }

    @Override
    public OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            getImgStackCollectionAsOperationWithProgressReporter() {
        return operationImgStackCollection;
    }

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {
        return delegate.doFind(manifestRecorder);
    }

    @Override
    public boolean exists() {
        return delegate.exists();
    }
}
