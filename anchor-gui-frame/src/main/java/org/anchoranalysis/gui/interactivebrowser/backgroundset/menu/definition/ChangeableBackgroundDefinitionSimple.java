/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

public class ChangeableBackgroundDefinitionSimple
        extends ChangeableBackgroundDefinitionWithDefault {

    public ChangeableBackgroundDefinitionSimple(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        super(backgroundSet);
    }

    @Override
    public IGetNames names(ErrorReporter errorReporter) {
        return new NamesFromBackgroundSet(getBackgroundSet(), errorReporter);
    }

    @Override
    public IImageStackCntrFromName stackCntrFromName(ErrorReporter errorReporter) {
        return new StackFromBackgroundSet(getBackgroundSet(), errorReporter);
    }
}
