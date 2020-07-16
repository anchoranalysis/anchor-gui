/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

@FunctionalInterface
public interface IModuleCreator {

    VideoStatsModule createVideoStatsModule() throws VideoStatsModuleCreateException;
}
