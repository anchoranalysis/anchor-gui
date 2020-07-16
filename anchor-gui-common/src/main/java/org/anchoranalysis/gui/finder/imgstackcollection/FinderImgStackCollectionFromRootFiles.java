/* (C)2020 */
package org.anchoranalysis.gui.finder.imgstackcollection;

import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.finder.FinderRasterFilesByManifestDescriptionFunction;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;

// Finds an image stack collection from the files in the root directory
public class FinderImgStackCollectionFromRootFiles implements FinderImgStackCollection {

    private FinderRasterFilesByManifestDescriptionFunction delegate;

    private CachedOperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            operationImgStackCollection =
                    new WrapOperationWithProgressReporterAsCached<>(
                            pr -> delegate.createStackCollection());

    public FinderImgStackCollectionFromRootFiles(RasterReader rasterReader, String function) {
        delegate = new FinderRasterFilesByManifestDescriptionFunction(rasterReader, function);
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
