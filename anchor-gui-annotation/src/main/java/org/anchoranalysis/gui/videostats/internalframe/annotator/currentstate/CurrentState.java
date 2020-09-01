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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ConfirmResetStateChangedListener;
import org.anchoranalysis.mpp.mark.ColoredMarks;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;

class CurrentState implements IQuerySelectedPoints {

    private SaveMonitor saveMonitor;

    private PartitionedMarks marks = new PartitionedMarks();
    private MarkCollection currentProposedMarks = new MarkCollection();
    private MarkCollection currentSelectedPointsMarks = new MarkCollection();
    private ColoredMarks currentMarksDisplayed = null;
    private MarkCollection listForRefresh = new MarkCollection();

    private static RGBColor colorAccepted = new RGBColor(Color.RED);
    private static RGBColor colorRejected = new RGBColor(Color.PINK);
    private static RGBColor colorSelectedPoints = new RGBColor(Color.YELLOW);
    private static RGBColor colorBlue = new RGBColor(Color.BLUE);

    private ConfirmReset confirmReset = new ConfirmReset();

    public CurrentState(SaveMonitor saveMonitor) {
        super();
        this.saveMonitor = saveMonitor;
    }

    public CurrentState copyForUndo() {
        CurrentState out = new CurrentState(saveMonitor);
        out.marks = marks.shallowCopy();
        out.currentProposedMarks = currentProposedMarks.shallowCopy();
        out.currentSelectedPointsMarks = currentSelectedPointsMarks.shallowCopy();
        out.currentMarksDisplayed =
                currentMarksDisplayed != null ? currentMarksDisplayed.shallowCopy() : null;

        out.listForRefresh = listForRefresh.shallowCopy();
        return out;
    }

    public void dispose() {
        confirmReset.dispose();
    }

    public void initAcceptedMarks(PartitionedMarks in) {
        // We don't alter the changedSinceLastSave variable
        marks.addAll(in);
        listForRefresh.addAll(in.getMarksAccepted());
        listForRefresh.addAll(in.getMarksRejected());
    }

    public boolean hasCurrentProposedState() {
        return currentProposedMarks != null && currentProposedMarks.size() > 0;
    }

    public void addCurrentProposedMarksFromSelectedPoints(Mark mark) {
        if (currentMarksDisplayed != null) {
            listForRefresh.addAll(currentMarksDisplayed.getMarks());
        }
        listForRefresh.addAll(new MarkCollection(mark));
        this.currentProposedMarks = new MarkCollection(mark);
        this.currentMarksDisplayed = new ColoredMarks(mark, colorBlue);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public void replaceCurrentProposedMarks(MarkCollection marksCore, ColoredMarks marksDisplayed) {

        if (this.currentMarksDisplayed != null) {
            listForRefresh.addAll(currentMarksDisplayed.getMarks());
        }

        listForRefresh.addAll(currentSelectedPointsMarks);
        this.currentSelectedPointsMarks = new MarkCollection();

        this.currentProposedMarks = marksCore;
        this.currentMarksDisplayed = marksDisplayed;
        listForRefresh.addAll(currentMarksDisplayed.getMarks());
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public void removeCurrentProposedMarks() {

        if (this.currentMarksDisplayed != null) {
            listForRefresh.addAll(currentMarksDisplayed.getMarks());
        }

        listForRefresh.addAll(currentSelectedPointsMarks);
        this.currentSelectedPointsMarks = new MarkCollection();

        this.currentProposedMarks = new MarkCollection();
        this.currentMarksDisplayed = new ColoredMarks();
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public ColoredMarks generateFullMarks() {
        ColoredMarks coloredMarks = new ColoredMarks();
        if (currentMarksDisplayed != null) {
            coloredMarks.addAll(currentMarksDisplayed);
        }
        coloredMarks.addAll(marks.getMarksAccepted(), colorAccepted);
        coloredMarks.addAll(marks.getMarksRejected(), colorRejected);
        coloredMarks.addAll(currentSelectedPointsMarks, colorSelectedPoints);
        return coloredMarks;
    }

    public void addSelectedPoint(Mark mark) {
        currentSelectedPointsMarks.add(mark);
        currentProposedMarks = new MarkCollection();
        currentMarksDisplayed = null;
        listForRefresh.add(mark);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public void removeAcceptedMarksAndSelectedPoints(MarkCollection marks, List<Point3i> points) {
        for (Mark m : marks) {
            removeAcceptedMark(m);
        }
        removeSelectedPoints(points);
    }

    // NB This is not the most efficient, as we have to search a linked list each time to find the
    // mark
    //  but as we don't call it often, it shouldn't be so bad
    private void removeAcceptedMark(Mark mark) {
        saveMonitor.markAsChanged();

        marks.removeFromEither(mark);

        listForRefresh.add(mark);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    // NB This is not the most efficient, as we have to search a linked list each time to find the
    // mark
    //  but as we don't call it often, it shouldn't be so bad
    private void removeSelectedPoints(List<Point3i> points) {

        MarkCollection toDelete = new MarkCollection();

        points.forEach(
                point -> {
                    int index = indexOfSelectedPoints(point);
                    toDelete.add(currentSelectedPointsMarks.get(index));
                    currentSelectedPointsMarks.remove(index);
                });

        currentProposedMarks = new MarkCollection();
        currentMarksDisplayed = null;

        listForRefresh.addAll(toDelete);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public MarkCollection getRefreshListAndReset() {
        MarkCollection refreshList = listForRefresh;
        listForRefresh = new MarkCollection();

        if (currentMarksDisplayed != null) {
            listForRefresh.addAll(currentMarksDisplayed.getMarks());
        }
        return refreshList;
    }

    public int indexOfSelectedPoints(Point3i point) {
        for (int i = 0; i < currentSelectedPointsMarks.size(); i++) {
            Mark m = currentSelectedPointsMarks.get(i);

            Point3d cp = m.centerPoint();
            if (cp.x() == point.x() && cp.y() == point.y() && cp.z() == point.z()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean hasSelectedPoints() {
        return currentSelectedPointsMarks != null && currentSelectedPointsMarks.size() > 0;
    }

    @Override
    public List<Point3i> selectedPointsAsIntegers() {
        List<Point3i> listOut = new ArrayList<>();
        for (Mark m : currentSelectedPointsMarks) {
            Point3d cp = m.centerPoint();
            listOut.add(new Point3i((int) cp.x(), (int) cp.y(), (int) cp.z()));
        }
        return listOut;
    }

    @Override
    public List<Point3f> selectedPointsAsFloats() {
        List<Point3f> listOut = new ArrayList<>();
        for (Mark m : currentSelectedPointsMarks) {
            Point3d cp = m.centerPoint();
            listOut.add(new Point3f((float) cp.x(), (float) cp.y(), (float) cp.z()));
        }
        return listOut;
    }

    public void markAsChanged() {
        saveMonitor.markAsChanged();
    }

    public MarkCollection getProposedMarks() {
        return currentProposedMarks;
    }

    public IConfirmReset confirmReset() {
        return confirmReset;
    }

    public QueryAcceptedRejected queryAcceptReject() {
        return marks;
    }

    private class ConfirmReset implements IConfirmReset {

        private EventListenerList listeners = new EventListenerList();

        @Override
        public boolean canConfirm() {
            return (currentSelectedPointsMarks.size() > 0 || currentProposedMarks.size() > 0);
        }

        @Override
        public boolean canReset() {
            return ((currentMarksDisplayed != null && currentMarksDisplayed.size() > 0)
                    || currentSelectedPointsMarks.size() > 0
                    || currentProposedMarks.size() > 0);
        }

        // Resets any unconfirmed activity
        @Override
        public void reset() {
            currentProposedMarks = new MarkCollection();
            if (currentMarksDisplayed != null) {
                listForRefresh.addAll(currentMarksDisplayed.getMarks());
            }
            listForRefresh.addAll(currentSelectedPointsMarks);
            currentMarksDisplayed = null;
            currentSelectedPointsMarks = new MarkCollection();
            triggerConfirmResetStateChangedEvent();
        }

        @Override
        public void addConfirmResetStateChangedListener(ConfirmResetStateChangedListener e) {
            listeners.add(ConfirmResetStateChangedListener.class, e);
        }

        @Override
        public boolean confirm(boolean accepted) {
            saveMonitor.markAsChanged();
            marks.addAll(accepted, currentProposedMarks.deepCopy());

            currentProposedMarks = new MarkCollection();
            if (currentMarksDisplayed != null) {
                listForRefresh.addAll(currentMarksDisplayed.getMarks());
            }
            listForRefresh.addAll(currentSelectedPointsMarks);
            currentMarksDisplayed = null;
            currentSelectedPointsMarks = new MarkCollection();
            triggerConfirmResetStateChangedEvent();
            return true;
        }

        public void triggerConfirmResetStateChangedEvent() {
            for (ConfirmResetStateChangedListener e :
                    listeners.getListeners(ConfirmResetStateChangedListener.class)) {
                e.confirmResetStateChanged();
            }
        }

        public void dispose() {
            listeners = null;
        }
    }
}
