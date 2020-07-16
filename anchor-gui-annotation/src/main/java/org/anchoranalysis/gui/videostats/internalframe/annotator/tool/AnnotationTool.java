/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

public abstract class AnnotationTool {

    //
    // Optional action, when leftMouseClickedoccurs
    //
    // NB when we have a evaluatorWithContextGetter() then the evaluation occurs elsewhere
    //   and we don't need to handle this here
    //
    // So for most purposes one would specify either leftMouseClicked or evaluatorWithContextGetter
    // but not both
    //
    public abstract void leftMouseClickedAtPoint(Point3d point);

    public abstract void proposed(ProposedCfg proposedCfg);

    public abstract void confirm(boolean accepted);

    public abstract Optional<EvaluatorWithContext> evaluatorWithContextGetter();
}
