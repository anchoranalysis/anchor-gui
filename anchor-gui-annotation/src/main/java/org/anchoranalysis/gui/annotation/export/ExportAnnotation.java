/* (C)2020 */
package org.anchoranalysis.gui.annotation.export;

import java.nio.file.Path;
import javax.swing.JFrame;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;

public abstract class ExportAnnotation {

    /** Do we prompt the user to ask if they want to scale */
    public abstract boolean isPromptForScalingNeeded();

    /** What file-path is proposed to the user first in a dialog */
    public abstract Path proposedExportPath();

    public abstract void exportToPath(
            Path path,
            double scaleFactor,
            JFrame parentFrame,
            AnnotationRefresher annotationRefresher)
            throws OperationFailedException;
}
