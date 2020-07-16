/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQueryAcceptedRejected;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQuerySelectedPoints;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IReplaceRemove;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ISwitchToGuessOrSelectPoints;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

// Membership is always based upon SUBMARK_CORE
public class DeleteTool extends AnnotationTool {

    private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;
    private RegionMap regionMap = RegionMapSingleton.instance();

    private ISwitchToGuessOrSelectPoints switcher;
    private IQuerySelectedPoints selectedPoints;
    private IQueryAcceptedRejected queryAcceptReject;
    private IReplaceRemove replaceRemove;

    public DeleteTool(
            IQueryAcceptedRejected queryAcceptReject,
            IQuerySelectedPoints selectedPoints,
            IReplaceRemove replaceRemove,
            ISwitchToGuessOrSelectPoints panelTool) {
        super();
        this.switcher = panelTool;
        this.selectedPoints = selectedPoints;
        this.queryAcceptReject = queryAcceptReject;
        this.replaceRemove = replaceRemove;
    }

    @Override
    public void leftMouseClickedAtPoint(Point3d point) {

        Cfg cfg = new Cfg();
        cfg.addAll(queryAcceptReject.getCfgAccepted());
        cfg.addAll(queryAcceptReject.getCfgRejected());

        Cfg marksToRemove = FindPoints.findMarksContainingPoint(cfg, point, regionMap, regionID);

        List<Point3i> selectedPointsToRemove =
                FindPoints.findSelectedPointsNear(point, selectedPoints);

        if (!marksToRemove.isEmpty() || !selectedPointsToRemove.isEmpty()) {
            replaceRemove.removeAcceptedMarksAndSelectedPoints(
                    marksToRemove, selectedPointsToRemove);
        }
    }

    @Override
    public void proposed(ProposedCfg proposedCfg) {
        // This should never be called as EvaluatorWithContext is null
    }

    @Override
    public void confirm(boolean accepted) {
        switcher.switchToGuessOrSelectPoints();

        // We implement the same behavior as the Guess tool as its not so clear what else it could
        // mean
        // GuessTool.confirmOnCurrentStateDisplayer(currentStateDisplayer);
    }

    @Override
    public Optional<EvaluatorWithContext> evaluatorWithContextGetter() {
        return Optional.empty();
    }
}
