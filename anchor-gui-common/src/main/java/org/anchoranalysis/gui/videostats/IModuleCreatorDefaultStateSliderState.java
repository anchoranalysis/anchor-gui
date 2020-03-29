package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

@FunctionalInterface
public interface IModuleCreatorDefaultStateSliderState {

	VideoStatsModule createVideoStatsModule(
		DefaultModuleState defaultFrameState,
		ISliderState sliderState
	) throws VideoStatsModuleCreateException;
}
