/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import javax.swing.JFrame;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

public class AdderGUICountToolbar implements IAddVideoStatsModule {

    private IAddVideoStatsModule delegate;
    private OpenedFileCounter openedFileCounter;

    public AdderGUICountToolbar(
            IAddVideoStatsModule delegate, OpenedFileCounter openedFileCounter) {
        super();
        this.delegate = delegate;
        this.openedFileCounter = openedFileCounter;
    }

    @Override
    public void addVideoStatsModule(VideoStatsModule module) {
        openedFileCounter.addVideoStatsModule(module);
        delegate.addVideoStatsModule(module);
    }

    @Override
    public VideoStatsModuleSubgroup getSubgroup() {
        return delegate.getSubgroup();
    }

    @Override
    public JFrame getParentFrame() {
        return delegate.getParentFrame();
    }

    @Override
    public IAddVideoStatsModule createChild() {
        return new AdderGUICountToolbar(delegate.createChild(), openedFileCounter);
    }
}
