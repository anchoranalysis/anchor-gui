/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.JFrame;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

public interface IAddVideoStatsModule {

    void addVideoStatsModule(VideoStatsModule module);

    VideoStatsModuleSubgroup getSubgroup();

    JFrame getParentFrame();

    IAddVideoStatsModule createChild();
}
