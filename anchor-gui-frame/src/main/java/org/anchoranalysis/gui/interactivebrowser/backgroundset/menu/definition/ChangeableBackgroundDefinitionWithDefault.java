/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;

public abstract class ChangeableBackgroundDefinitionWithDefault
        extends ChangeableBackgroundDefinition {

    private OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet;

    public ChangeableBackgroundDefinitionWithDefault(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            getBackgroundSet() {
        return backgroundSet;
    }

    public void update(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        this.backgroundSet = backgroundSet;
    }
}
