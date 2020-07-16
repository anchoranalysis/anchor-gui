/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

public abstract class ChangeableBackgroundDefinition {

    public abstract void update(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet);

    public abstract IGetNames names(ErrorReporter errorReporter);

    public abstract IImageStackCntrFromName stackCntrFromName(ErrorReporter errorReporter);
}
