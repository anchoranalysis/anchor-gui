/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.gui.videostats.internalframe.annotator.undoredo.IRecordSnapshot;

class SnapshotChangeSelectedPoints implements IChangeSelectedPoints {

    private IChangeSelectedPoints delegate;
    private IRecordSnapshot recorder;

    public SnapshotChangeSelectedPoints(IChangeSelectedPoints delegate, IRecordSnapshot recorder) {
        super();
        this.delegate = delegate;
        this.recorder = recorder;
    }

    @Override
    public void addCurrentProposedCfgFromSelectedPoints(Mark mark) {
        recorder.recordSnapshot();
        delegate.addCurrentProposedCfgFromSelectedPoints(mark);
    }

    @Override
    public void addSelectedPoint(Mark mark) {
        recorder.recordSnapshot();
        delegate.addSelectedPoint(mark);
    }
}
