/* (C)2020 */
package org.anchoranalysis.gui.finder.imgstackcollection;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.cache.WrapOperationWithProgressReporterAsCached;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.ManifestRecorder;

// Combines a number of other FinderImgStackCollection
public class FinderImgStackCollectionCombine implements FinderImgStackCollection {

    private List<FinderImgStackCollection> list = new ArrayList<>();

    private CachedOperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            operation =
                    new WrapOperationWithProgressReporterAsCached<>(
                            pr -> {
                                NamedImgStackCollection out = new NamedImgStackCollection();

                                for (FinderImgStackCollection finder : list) {
                                    try {
                                        out.addFrom(finder.getImgStackCollection());
                                    } catch (GetOperationFailedException e) {
                                        throw new OperationFailedException(e);
                                    }
                                }

                                return out;
                            });

    @Override
    public NamedProvider<Stack> getImgStackCollection() throws GetOperationFailedException {
        try {
            return operation.doOperation(ProgressReporterNull.get());
        } catch (OperationFailedException e) {
            throw new GetOperationFailedException(e);
        }
    }

    @Override
    public OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            getImgStackCollectionAsOperationWithProgressReporter() {
        return operation;
    }

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {
        boolean result = false;
        for (FinderImgStackCollection finder : list) {
            if (finder.doFind(manifestRecorder)) {
                result = true;
            }
        }
        return result;
    }

    // Uses OR behaviour, so returns TRUE if any of the elements exist
    @Override
    public boolean exists() {
        for (FinderImgStackCollection finder : list) {
            if (finder.exists()) {
                return true;
            }
        }
        return false;
    }

    public void add(FinderImgStackCollection finder) {
        list.add(finder);
    }
}
