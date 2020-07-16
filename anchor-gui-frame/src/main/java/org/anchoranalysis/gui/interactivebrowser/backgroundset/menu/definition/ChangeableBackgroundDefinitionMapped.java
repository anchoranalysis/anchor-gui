/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

public class ChangeableBackgroundDefinitionMapped
        extends ChangeableBackgroundDefinitionWithDefault {

    private StringMap labelMap;

    public ChangeableBackgroundDefinitionMapped(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet,
            StringMap labelMap) {
        super(backgroundSet);
        this.labelMap = labelMap;
    }

    @Override
    public IGetNames names(ErrorReporter errorReporter) {
        return new NamesFromMap(labelMap, getBackgroundSet(), errorReporter);
    }

    @Override
    public IImageStackCntrFromName stackCntrFromName(ErrorReporter errorReporter) {
        return new StackFromBackgroundSetViaMap(labelMap, getBackgroundSet(), errorReporter);
    }
}
