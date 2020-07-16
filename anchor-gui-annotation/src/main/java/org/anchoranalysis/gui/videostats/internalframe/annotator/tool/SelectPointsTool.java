/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.InsufficientPointsException;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitterException;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IAcceptProposal;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IChangeSelectedPoints;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQuerySelectedPoints;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.extent.ImageDimensions;

@RequiredArgsConstructor
public class SelectPointsTool extends AnnotationTool {

    private final Optional<EvaluatorWithContext> evaluator;
    private final IChangeSelectedPoints changeSelectedPoints;
    private final PointsFitter pointsFitter;
    private final IAcceptProposal acceptProposal;
    private final IQuerySelectedPoints selectedPoints;
    private final ToolErrorReporter errorReporter;

    private ImageDimensions dimensions;

    @Override
    public void leftMouseClickedAtPoint(Point3d point) {
        // NOTHING TO DO
    }

    @Override
    public void proposed(ProposedCfg proposedCfg) {

        dimensions = proposedCfg.getDimensions();

        // Extract what should be the only mark
        assert (proposedCfg.getCfgCore().getMarks().size() == 1);
        Mark m = proposedCfg.getCfgCore().getMarks().get(0);

        changeSelectedPoints.addSelectedPoint(m);
    }

    @Override
    public void confirm(boolean accepted) {

        if (acceptProposal.confirm(accepted)) {
            return; // DONE
        }

        if (selectedPoints.hasSelectedPoints()) {

            if (dimensions == null) {
                errorReporter.showError(
                        SelectPointsTool.class, "Incorrect initialization", "dimensions are null");
            }

            if (evaluator.isPresent()) {
                proposeCfgFromPoints(evaluator.get());
            } else {
                errorReporter.showError(
                        SelectPointsTool.class,
                        "Incorrect initialization",
                        "No evaluator is defined");
            }
        }
    }

    private void proposeCfgFromPoints(EvaluatorWithContext eval) {

        try {
            changeSelectedPoints.addCurrentProposedCfgFromSelectedPoints(proposeMark(eval));
        } catch (PointsFitterException e) {

            if (e.getCause() == null) {
                errorReporter.showError(SelectPointsTool.class, "Unknown error", e.getMessage());
            } else {
                // If we have a cause behind it, we don't know what error is causing it, but
                //   for the sake of giving user instructions we give this message, which
                //   will usually solve the problem
                //   especially if it's  *IllegalArgumentException: Matrix is singular*
                errorReporter.showError("Add more points (in many z-stacks)!");
            }

        } catch (InsufficientPointsException e) {
            errorReporter.showError("Add more points (in many z-stacks)!");
        }
    }

    private Mark proposeMark(EvaluatorWithContext eval)
            throws PointsFitterException, InsufficientPointsException {
        Mark mark = eval.getCfgGen().getTemplateMark().create();
        pointsFitter.fit(selectedPoints.selectedPointsAsFloats(), mark, dimensions);
        return mark;
    }

    @Override
    public Optional<EvaluatorWithContext> evaluatorWithContextGetter() {
        return evaluator;
    }
}
