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

package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import java.util.List;
import org.anchoranalysis.annotation.mark.DualMarks;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ConfirmResetStateChangedListener;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo.IUndoRedo;
import org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo.UndoRedoRecorder;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.ColoredMarks;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.spatial.point.Point3i;

public class CurrentStateDisplayer {

    private ShowCurrentState marksShower;
    private CurrentState currentState;
    private OverlapChecker overlapChecker;

    private boolean alreadyConfirmedOnce = false;

    private UndoRedoRecorder<CurrentState> undoRedo =
            new UndoRedoRecorder<>(
                    () -> marksShower.showRedrawAll(currentState),
                    currentState::copyForUndo,
                    stateToAssign -> {
                        currentState = stateToAssign;
                        currentState.markAsChanged();
                    });

    private IReplaceRemove replaceRemove = new SnapshotReplaceRemove(new ReplaceRemove(), undoRedo);

    private IChangeSelectedPoints changeSelectedPoints =
            new SnapshotChangeSelectedPoints(new ChangeSelectedPoints(), undoRedo);

    private IConfirmReset confirmReset;

    public CurrentStateDisplayer(
            ShowCurrentState marksShower,
            SaveMonitor saveMonitor,
            Dimensions dimensions,
            RegionMap regionMap,
            ToolErrorReporter errorReporter) {
        this.marksShower = marksShower;
        this.currentState = new CurrentState(saveMonitor);
        this.overlapChecker = new OverlapChecker(dimensions, regionMap, errorReporter);
        this.confirmReset = new WrapConfirmReset(currentState.confirmReset());
    }

    public void init(PartitionedMarks marks) {
        currentState.initAcceptedMarks(marks);
        marksShower.show(currentState);
    }

    public DualMarks queryAcceptReject() {
        return currentState.queryAcceptReject();
    }

    public void dispose() {
        marksShower = null;
        if (currentState != null) {
            currentState.dispose();
            currentState = null;
        }
        undoRedo.dispose();
    }

    public IUndoRedo undoRedo() {
        return undoRedo;
    }

    public IReplaceRemove replaceRemove() {
        return replaceRemove;
    }

    public IQuerySelectedPoints querySelectedPoints() {
        return currentState;
    }

    public IChangeSelectedPoints changeSelectedPoints() {
        return changeSelectedPoints;
    }

    public IConfirmReset confirmReset() {
        return confirmReset;
    }

    private class ReplaceRemove implements IReplaceRemove {

        @Override
        public void replaceCurrentProposedMarks(
                MarkCollection marksCore, ColoredMarks marksDisplayed, int sliceZ) {
            currentState.replaceCurrentProposedMarks(marksCore, marksDisplayed);
            marksShower.showAtSlice(currentState, sliceZ);
            alreadyConfirmedOnce = false;
        }

        @Override
        public void removeCurrentProposedMarks() {
            currentState.removeCurrentProposedMarks();
            marksShower.show(currentState);
            alreadyConfirmedOnce = false;
        }

        @Override
        public void removeAcceptedMarksAndSelectedPoints(
                MarkCollection marks, List<Point3i> points) {
            currentState.removeAcceptedMarksAndSelectedPoints(marks, points);
            marksShower.show(currentState);
            alreadyConfirmedOnce = false;
        }
    }

    private class ChangeSelectedPoints implements IChangeSelectedPoints {

        @Override
        public void addCurrentProposedMarksFromSelectedPoints(Mark mark) {
            currentState.addCurrentProposedMarksFromSelectedPoints(mark);
            marksShower.showAtSlice(currentState, (int) mark.centerPoint().z());
            alreadyConfirmedOnce = false;
        }

        @Override
        public void addSelectedPoint(Mark mark) {
            currentState.addSelectedPoint(mark);
            marksShower.show(currentState);
            alreadyConfirmedOnce = false;
        }
    }

    private class WrapConfirmReset implements IConfirmReset {

        private IConfirmReset delegate;

        public WrapConfirmReset(IConfirmReset delegate) {
            super();
            this.delegate = delegate;
        }

        @Override
        public boolean canConfirm() {
            return delegate.canConfirm();
        }

        @Override
        public boolean confirm(boolean accepted) {
            return currentState.hasCurrentProposedState() && confirmProposal(accepted);
        }

        private boolean confirmProposal(boolean accepted) {

            if (!alreadyConfirmedOnce && hasLargeOverlap()) {

                // We prompt the user to make they want to continue because of the large overlap
                marksShower.showError("Large overlap. Plase confirm a second-time.");
                alreadyConfirmedOnce = true;
                return false;
            }

            alreadyConfirmedOnce = false;

            undoRedo.recordSnapshot();
            delegate.confirm(accepted);
            marksShower.show(currentState);
            return true;
        }

        private boolean hasLargeOverlap() {
            return overlapChecker.hasLargeOverlap(
                    currentState.getProposedMarks(), currentState.queryAcceptReject().accepted());
        }

        @Override
        public boolean canReset() {
            return delegate.canReset();
        }

        @Override
        public void reset() {
            undoRedo.recordSnapshot();
            delegate.reset();
            marksShower.show(currentState);
            alreadyConfirmedOnce = false;
        }

        @Override
        public void addConfirmResetStateChangedListener(ConfirmResetStateChangedListener e) {
            delegate.addConfirmResetStateChangedListener(e);
        }
    }
}
