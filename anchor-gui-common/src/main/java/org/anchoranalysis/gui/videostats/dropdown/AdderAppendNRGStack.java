/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.JFrame;
import org.anchoranalysis.gui.videostats.INRGStackGetter;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleSubgroup;

public class AdderAppendNRGStack implements IAddVideoStatsModule {

    private final IAddVideoStatsModule delegate;
    private INRGStackGetter nrgStackGetter;

    public AdderAppendNRGStack(IAddVideoStatsModule adder, INRGStackGetter nrgStackGetter) {
        super();
        this.delegate = adder;
        this.nrgStackGetter = nrgStackGetter;
    }

    @Override
    public void addVideoStatsModule(VideoStatsModule module) {

        module.setNrgStackGetter(nrgStackGetter);
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
        return delegate.createChild();
    }
}
