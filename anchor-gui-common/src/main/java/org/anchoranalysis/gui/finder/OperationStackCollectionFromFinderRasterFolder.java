/* (C)2020 */
package org.anchoranalysis.gui.finder;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.CachedOperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.stack.Stack;

class OperationStackCollectionFromFinderRasterFolder
        extends CachedOperationWithProgressReporter<
                NamedProvider<Stack>, OperationFailedException> {

    private FinderRasterFolder finderRasterFolder;

    public OperationStackCollectionFromFinderRasterFolder(FinderRasterFolder finderRasterFolder) {
        super();
        this.finderRasterFolder = finderRasterFolder;
    }

    @Override
    protected NamedProvider<Stack> execute(ProgressReporter progressReporter)
            throws OperationFailedException {
        return finderRasterFolder.createStackCollection(true);
    }
}
