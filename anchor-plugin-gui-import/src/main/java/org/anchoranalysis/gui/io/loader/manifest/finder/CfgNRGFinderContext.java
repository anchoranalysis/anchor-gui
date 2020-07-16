/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import javax.swing.JFrame;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollection;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;

public class CfgNRGFinderContext {

    private FinderImgStackCollection finderImgStackCollection;
    private FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer;
    private JFrame parentFrame;
    private BoundOutputManagerRouteErrors outputManager;
    private VideoStatsModuleGlobalParams mpg;

    public CfgNRGFinderContext(
            FinderImgStackCollection finderImgStackCollection,
            FinderSerializedObject<KernelProposer<CfgNRGPixelized>> finderKernelProposer,
            JFrame parentFrame,
            BoundOutputManagerRouteErrors outputManager,
            VideoStatsModuleGlobalParams mpg) {
        super();
        this.finderImgStackCollection = finderImgStackCollection;
        this.finderKernelProposer = finderKernelProposer;
        this.parentFrame = parentFrame;
        this.outputManager = outputManager;
        this.mpg = mpg;
    }

    public FinderImgStackCollection getFinderImgStackCollection() {
        return finderImgStackCollection;
    }

    public FinderSerializedObject<KernelProposer<CfgNRGPixelized>> getFinderKernelProposer() {
        return finderKernelProposer;
    }

    public JFrame getParentFrame() {
        return parentFrame;
    }

    public BoundOutputManagerRouteErrors getOutputManager() {
        return outputManager;
    }

    public VideoStatsModuleGlobalParams getMpg() {
        return mpg;
    }
}
