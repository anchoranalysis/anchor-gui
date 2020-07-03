package org.anchoranalysis.gui.videostats.operation;

import java.util.Optional;

import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

public interface VideoStatsOperation {

	String getName();
	
	void execute(boolean withMessages);
	
	Optional<IVideoStatsOperationCombine> getCombiner();
}
