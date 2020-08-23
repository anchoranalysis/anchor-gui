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
import org.anchoranalysis.anchor.mpp.mark.ColoredMarks;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.ConfirmResetStateChangedListener;

class CurrentState implements IQuerySelectedPoints {

    private SaveMonitor saveMonitor;

    private DualCfg cfg = new DualCfg();
    private MarkCollection currentProposedCfg = new MarkCollection();
    private MarkCollection currentSelectedPointsCfg = new MarkCollection();
    private ColoredMarks currentCfgDisplayed = null;
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
        out.cfg = cfg.shallowCopy();
        out.currentProposedCfg = currentProposedCfg.shallowCopy();
        out.currentSelectedPointsCfg = currentSelectedPointsCfg.shallowCopy();
        out.currentCfgDisplayed =
                currentCfgDisplayed != null ? currentCfgDisplayed.shallowCopy() : null;

        out.listForRefresh = listForRefresh.shallowCopy();
        return out;
    }

    public void dispose() {
        confirmReset.dispose();
    }

    public void initAcceptedCfg(DualCfg in) {
        // We don't alter the changedSinceLastSave variable
        cfg.addAll(in);
        listForRefresh.addAll(in.getCfgAccepted());
        listForRefresh.addAll(in.getCfgRejected());
    }

    public boolean hasCurrentProposedState() {
        return currentProposedCfg != null && currentProposedCfg.size() > 0;
    }

    public void addCurrentProposedCfgFromSelectedPoints(Mark mark) {
        if (currentCfgDisplayed != null) {
            listForRefresh.addAll(currentCfgDisplayed.getMarks());
        }
        listForRefresh.addAll(new MarkCollection(mark));
        this.currentProposedCfg = new MarkCollection(mark);
        this.currentCfgDisplayed = new ColoredMarks(mark, colorBlue);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public void replaceCurrentProposedCfg(MarkCollection cfgCore, ColoredMarks cfgDisplayed) {

        if (this.currentCfgDisplayed != null) {
            listForRefresh.addAll(currentCfgDisplayed.getMarks());
        }

        listForRefresh.addAll(currentSelectedPointsCfg);
        this.currentSelectedPointsCfg = new MarkCollection();

        this.currentProposedCfg = cfgCore;
        this.currentCfgDisplayed = cfgDisplayed;
        listForRefresh.addAll(currentCfgDisplayed.getMarks());
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public void removeCurrentProposedCfg() {

        if (this.currentCfgDisplayed != null) {
            listForRefresh.addAll(currentCfgDisplayed.getMarks());
        }

        listForRefresh.addAll(currentSelectedPointsCfg);
        this.currentSelectedPointsCfg = new MarkCollection();

        this.currentProposedCfg = new MarkCollection();
        this.currentCfgDisplayed = new ColoredMarks();
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public ColoredMarks generateFullCfg() {
        ColoredMarks coloredCfg = new ColoredMarks();
        if (currentCfgDisplayed != null) {
            coloredCfg.addAll(currentCfgDisplayed);
        }
        coloredCfg.addAll(cfg.getCfgAccepted(), colorAccepted);
        coloredCfg.addAll(cfg.getCfgRejected(), colorRejected);
        coloredCfg.addAll(currentSelectedPointsCfg, colorSelectedPoints);
        return coloredCfg;
    }

    public void addSelectedPoint(Mark mark) {
        currentSelectedPointsCfg.add(mark);
        currentProposedCfg = new MarkCollection();
        currentCfgDisplayed = null;
        listForRefresh.add(mark);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public void removeAcceptedMarksAndSelectedPoints(MarkCollection cfg, List<Point3i> points) {
        for (Mark m : cfg) {
            removeAcceptedMark(m);
        }
        removeSelectedPoints(points);
    }

    // NB This is not the most efficient, as we have to search a linked list each time to find the
    // mark
    //  but as we don't call it often, it shouldn't be so bad
    private void removeAcceptedMark(Mark mark) {
        saveMonitor.markAsChanged();

        cfg.removeFromEither(mark);

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
                    toDelete.add(currentSelectedPointsCfg.get(index));
                    currentSelectedPointsCfg.remove(index);
                });

        currentProposedCfg = new MarkCollection();
        currentCfgDisplayed = null;

        listForRefresh.addAll(toDelete);
        confirmReset.triggerConfirmResetStateChangedEvent();
    }

    public MarkCollection getRefreshListAndReset() {
        MarkCollection refreshList = listForRefresh;
        listForRefresh = new MarkCollection();

        if (currentCfgDisplayed != null) {
            listForRefresh.addAll(currentCfgDisplayed.getMarks());
        }
        return refreshList;
    }

    public int indexOfSelectedPoints(Point3i point) {
        for (int i = 0; i < currentSelectedPointsCfg.size(); i++) {
            Mark m = currentSelectedPointsCfg.get(i);

            Point3d cp = m.centerPoint();
            if (cp.x() == point.x() && cp.y() == point.y() && cp.z() == point.z()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean hasSelectedPoints() {
        return currentSelectedPointsCfg != null && currentSelectedPointsCfg.size() > 0;
    }

    @Override
    public List<Point3i> selectedPointsAsIntegers() {
        List<Point3i> listOut = new ArrayList<>();
        for (Mark m : currentSelectedPointsCfg) {
            Point3d cp = m.centerPoint();
            listOut.add(new Point3i((int) cp.x(), (int) cp.y(), (int) cp.z()));
        }
        return listOut;
    }

    @Override
    public List<Point3f> selectedPointsAsFloats() {
        List<Point3f> listOut = new ArrayList<>();
        for (Mark m : currentSelectedPointsCfg) {
            Point3d cp = m.centerPoint();
            listOut.add(new Point3f((float) cp.x(), (float) cp.y(), (float) cp.z()));
        }
        return listOut;
    }

    public void markAsChanged() {
        saveMonitor.markAsChanged();
    }

    public MarkCollection getProposedCfg() {
        return currentProposedCfg;
    }

    public IConfirmReset confirmReset() {
        return confirmReset;
    }

    public IQueryAcceptedRejected queryAcceptReject() {
        return cfg;
    }

    private class ConfirmReset implements IConfirmReset {

        private EventListenerList listeners = new EventListenerList();

        @Override
        public boolean canConfirm() {
            return (currentSelectedPointsCfg.size() > 0 || currentProposedCfg.size() > 0);
        }

        @Override
        public boolean canReset() {
            return ((currentCfgDisplayed != null && currentCfgDisplayed.size() > 0)
                    || currentSelectedPointsCfg.size() > 0
                    || currentProposedCfg.size() > 0);
        }

        // Resets any unconfirmed activity
        @Override
        public void reset() {
            currentProposedCfg = new MarkCollection();
            if (currentCfgDisplayed != null) {
                listForRefresh.addAll(currentCfgDisplayed.getMarks());
            }
            listForRefresh.addAll(currentSelectedPointsCfg);
            currentCfgDisplayed = null;
            currentSelectedPointsCfg = new MarkCollection();
            triggerConfirmResetStateChangedEvent();
        }

        @Override
        public void addConfirmResetStateChangedListener(ConfirmResetStateChangedListener e) {
            listeners.add(ConfirmResetStateChangedListener.class, e);
        }

        @Override
        public boolean confirm(boolean accepted) {
            saveMonitor.markAsChanged();
            cfg.addAll(accepted, currentProposedCfg.deepCopy());

            currentProposedCfg = new MarkCollection();
            if (currentCfgDisplayed != null) {
                listForRefresh.addAll(currentCfgDisplayed.getMarks());
            }
            listForRefresh.addAll(currentSelectedPointsCfg);
            currentCfgDisplayed = null;
            currentSelectedPointsCfg = new MarkCollection();
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
