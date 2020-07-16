/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IAcceptProposal;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IReplaceRemove;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

@AllArgsConstructor
public class GuessTool extends AnnotationTool {

    private IReplaceRemove replaceRemove;
    private IAcceptProposal acceptProposal;
    private Optional<EvaluatorWithContext> evaluatorWithContext;
    private ToolErrorReporter toolErrorReporter;

    public boolean isEnabled() {
        return evaluatorWithContext.isPresent();
    }

    @Override
    public void proposed(ProposedCfg proposedCfg) {

        if (!isEnabled()) {
            return;
        }

        if (proposedCfg.isSuccess()) {
            replaceRemove.replaceCurrentProposedCfg(
                    proposedCfg.getCfgCore(),
                    OverlayCollectionMarkFactory.cfgFromOverlays(proposedCfg.getColoredCfg()),
                    proposedCfg.getSuggestedSliceNum());
        } else {
            replaceRemove.removeCurrentProposedCfg();
            toolErrorReporter.showError(
                    GuessTool.class,
                    "Guess failed. Try again (or select points)!",
                    proposedCfg.getPfd().describe());
        }
    }

    @Override
    public void confirm(boolean accepted) {

        if (!isEnabled()) {
            return;
        }

        acceptProposal.confirm(accepted);
    }

    @Override
    public Optional<EvaluatorWithContext> evaluatorWithContextGetter() {
        return evaluatorWithContext;
    }

    @Override
    public void leftMouseClickedAtPoint(Point3d point) {
        // NOTHING TO DO
    }
}
