/* (C)2020 */
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
                                NRGStackWithParams nrgStackWithParams =
                                        delegate.operationNrgStackWithProgressReporter()
                                                .doOperation(pr);
                                return nrgStackWithParams
                                        .getNrgStack()
                                        .asStack()
                                        .extractUpToThreeChnls();
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
                                            "nrgStack", operationExtractUntilThreeChnls);

                                    try {
                                        stackCollection.addFromWithPrefix(
                                                delegate.getNamedImgStackCollection(), "nrgChnl-");
                                    } catch (GetOperationFailedException e) {
                                        throw new OperationFailedException(e);
                                    }
                                }
                                return stackCollection;
                            });

    public FinderImgStackCollectionFromNrgStack(FinderNrgStack finderNrgStack) {
        this.delegate = finderNrgStack;
    }

    @Override
    public NamedProvider<Stack> getImgStackCollection() throws GetOperationFailedException {
        try {
            return operationImgStackCollection.doOperation(ProgressReporterNull.get());
        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(e);
        }
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
