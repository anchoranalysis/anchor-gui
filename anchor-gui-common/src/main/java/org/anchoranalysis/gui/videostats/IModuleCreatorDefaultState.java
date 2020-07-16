/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

@FunctionalInterface
public interface IModuleCreatorDefaultState {

    VideoStatsModule createVideoStatsModule(DefaultModuleState defaultFrameState)
            throws VideoStatsModuleCreateException;
}
