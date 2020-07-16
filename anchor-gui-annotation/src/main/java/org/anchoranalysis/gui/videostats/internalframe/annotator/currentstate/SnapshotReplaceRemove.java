/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import java.util.List;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo.IRecordSnapshot;

class SnapshotReplaceRemove implements IReplaceRemove {

    private IReplaceRemove delegate;
    private IRecordSnapshot recorder;

    public SnapshotReplaceRemove(IReplaceRemove delegate, IRecordSnapshot recorder) {
        super();
        this.delegate = delegate;
        this.recorder = recorder;
    }

    @Override
    public void removeCurrentProposedCfg() {
        recorder.recordSnapshot();
        delegate.removeCurrentProposedCfg();
    }

    @Override
    public void replaceCurrentProposedCfg(Cfg cfgCore, ColoredCfg cfgDisplayed, int sliceZ) {
        recorder.recordSnapshot();
        delegate.removeCurrentProposedCfg();
    }

    @Override
    public void removeAcceptedMarksAndSelectedPoints(Cfg cfg, List<Point3i> points) {
        recorder.recordSnapshot();
        delegate.removeAcceptedMarksAndSelectedPoints(cfg, points);
    }
}
