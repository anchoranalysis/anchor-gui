package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.anchor.mpp.mark.Mark;

public interface IChangeSelectedPoints {

	void addCurrentProposedCfgFromSelectedPoints(Mark mark);
	
	void addSelectedPoint(Mark mark);
}
