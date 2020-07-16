/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.annotation.AnnotationBackground;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;

public abstract class AnnotationInitParams {

    public OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
            getBackgroundSetOp() {
        return getBackground().getBackgroundSetOp();
    }

    public abstract AnnotationBackground getBackground();
}
