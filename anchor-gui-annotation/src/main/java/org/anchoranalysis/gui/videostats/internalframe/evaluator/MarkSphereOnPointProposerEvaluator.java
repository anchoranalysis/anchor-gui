/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.awt.Color;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkSphere;
import org.anchoranalysis.anchor.mpp.overlay.OverlayMark;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.extent.ImageDimensions;

@RequiredArgsConstructor
public class MarkSphereOnPointProposerEvaluator implements ProposalOperationCreator {

    // START REQUIRED ARGUMENTS
    private final ImageDimensions dimensions;
    // END REQUIRED ARGUMENTS

    private RGBColor colorMark = new RGBColor(Color.YELLOW);

    private static RegionMembershipWithFlags regionMembership =
            RegionMapSingleton.instance()
                    .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE);

    @Override
    public ProposalOperation create(
            Cfg cfg, final Point3d position, ProposerContext context, CfgGen cfgGen)
            throws OperationFailedException {

        return errorNode -> {
            ProposedCfg proposedCfg = new ProposedCfg();
            proposedCfg.setDimensions(dimensions);
            proposedCfg.setSuccess(true);

            MarkSphere markSphere = new MarkSphere();
            markSphere.setRadius(1);
            markSphere.setPos(position);

            proposedCfg.getCfgCore().add(markSphere);
            proposedCfg.getCfgToRedraw().add(markSphere);
            proposedCfg
                    .getColoredCfg()
                    .add(new OverlayMark(markSphere, regionMembership), colorMark);
            proposedCfg.setSuggestedSliceNum((int) position.getZ());

            return proposedCfg;
        };
    }
}
