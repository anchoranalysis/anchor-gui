/* (C)2020 */
package org.anchoranalysis.gui.finder.imgstackcollection;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.finder.Finder;

public interface FinderImgStackCollection extends Finder {

    NamedProvider<Stack> getImgStackCollection() throws GetOperationFailedException;

    OperationWithProgressReporter<NamedProvider<Stack>, OperationFailedException>
            getImgStackCollectionAsOperationWithProgressReporter();
}
