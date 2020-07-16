/* (C)2020 */
package org.anchoranalysis.gui.frame.multiraster;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;

public class NamedRasterSet {
    private String name;
    private OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet;

    public NamedRasterSet(
            String name,
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        super();
        this.name = name;
        this.backgroundSet = backgroundSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        this.backgroundSet = backgroundSet;
    }
}
