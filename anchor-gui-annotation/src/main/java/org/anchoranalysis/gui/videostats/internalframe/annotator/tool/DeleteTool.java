/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQuerySelectedPoints;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IReplaceRemove;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.QueryAcceptedRejected;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ISwitchToGuessOrSelectPoints;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.MarkCollection;

// Membership is always based upon SUBMARK_CORE
public class DeleteTool extends AnnotationTool {

    private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;
    private RegionMap regionMap = RegionMapSingleton.instance();

    private ISwitchToGuessOrSelectPoints switcher;
    private IQuerySelectedPoints selectedPoints;
    private QueryAcceptedRejected queryAcceptReject;
    private IReplaceRemove replaceRemove;

    public DeleteTool(
            QueryAcceptedRejected queryAcceptReject,
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

        MarkCollection marks = new MarkCollection();
        marks.addAll(queryAcceptReject.getMarksAccepted());
        marks.addAll(queryAcceptReject.getMarksRejected());

        MarkCollection marksToRemove =
                FindPoints.findMarksContainingPoint(marks, point, regionMap, regionID);

        List<Point3i> selectedPointsToRemove =
                FindPoints.findSelectedPointsNear(point, selectedPoints);

        if (!marksToRemove.isEmpty() || !selectedPointsToRemove.isEmpty()) {
            replaceRemove.removeAcceptedMarksAndSelectedPoints(
                    marksToRemove, selectedPointsToRemove);
        }
    }

    @Override
    public void proposed(ProposedMarks proposedMarks) {
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
