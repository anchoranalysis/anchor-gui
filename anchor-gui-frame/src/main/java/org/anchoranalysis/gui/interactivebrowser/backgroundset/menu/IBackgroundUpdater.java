/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;

@FunctionalInterface
public interface IBackgroundUpdater {

    void update(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet);
}
