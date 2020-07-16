/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import javax.swing.JFrame;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.frame.VideoStatsFrame;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

public class SubgrouppedAdder implements IAddVideoStatsModule {

    private VideoStatsModuleSubgroup subgroup;
    private VideoStatsFrame videoStatsFrame;

    public SubgrouppedAdder(
            VideoStatsFrame videoStatsFrame, DefaultModuleState defaultModuleState) {
        super();
        assert (defaultModuleState != null);
        assert (videoStatsFrame != null);
        this.videoStatsFrame = videoStatsFrame;
        this.subgroup = new VideoStatsModuleSubgroup(defaultModuleState);
    }

    // Creates an Adder attached to a new subgroup which inherits from the current
    @Override
    public IAddVideoStatsModule createChild() {
        return new SubgrouppedAdder(this.videoStatsFrame, subgroup.getDefaultModuleState().copy());
    }

    @Override
    public void addVideoStatsModule(VideoStatsModule module) {
        videoStatsFrame.addVideoStatsModuleVisible(module, subgroup);
    }

    @Override
    public VideoStatsModuleSubgroup getSubgroup() {
        return subgroup;
    }

    @Override
    public JFrame getParentFrame() {
        return videoStatsFrame;
    }
}
