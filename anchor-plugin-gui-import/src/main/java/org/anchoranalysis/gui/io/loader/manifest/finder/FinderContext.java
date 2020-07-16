/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import javax.swing.JFrame;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackgroundAdder;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;

public class FinderContext {

    private NRGBackgroundAdder<?> nrgBackground;
    private BoundVideoStatsModuleDropDown boundVideoStats;
    private VideoStatsOperationMenu parentMenu;
    private CfgNRGFinderContext context;

    public FinderContext(
            NRGBackgroundAdder<?> nrgBackground,
            BoundVideoStatsModuleDropDown boundVideoStats,
            VideoStatsOperationMenu parentMenu,
            CfgNRGFinderContext context) {
        super();
        this.nrgBackground = nrgBackground;
        this.boundVideoStats = boundVideoStats;
        this.parentMenu = parentMenu;
        this.context = context;
    }

    public NRGBackgroundAdder<?> getNrgBackground() {
        return nrgBackground;
    }

    public BoundVideoStatsModuleDropDown getBoundVideoStats() {
        return boundVideoStats;
    }

    public VideoStatsOperationMenu getParentMenu() {
        return parentMenu;
    }

    public CfgNRGFinderContext getContext() {
        return context;
    }

    public VideoStatsModuleGlobalParams getMpg() {
        return context.getMpg();
    }

    public JFrame getParentFrame() {
        return context.getParentFrame();
    }
}
