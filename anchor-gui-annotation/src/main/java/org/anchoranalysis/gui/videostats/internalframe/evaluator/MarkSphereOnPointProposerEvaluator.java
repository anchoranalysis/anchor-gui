/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;



import java.awt.Color;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkSphere;
import org.anchoranalysis.anchor.mpp.overlay.OverlayMark;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.extent.ImageDimensions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarkSphereOnPointProposerEvaluator implements ProposalOperationCreator {

	// START REQUIRED ARGUMENTS
	private final ImageDimensions dimensions;
	// END REQUIRED ARGUMENTS
	
	private RGBColor colorMark = new RGBColor(Color.YELLOW);
	
	private static RegionMembershipWithFlags regionMembership = RegionMapSingleton.instance().membershipWithFlagsForIndex(
		GlobalRegionIdentifiers.SUBMARK_INSIDE
	);
	
	@Override
	public ProposalOperation create(Cfg cfg, final Point3d position,
			ProposerContext context,
			CfgGen cfgGen)
			throws OperationFailedException {
		
		return errorNode -> {
			ProposedCfg proposedCfg = new ProposedCfg();
			proposedCfg.setDimensions( dimensions );
			proposedCfg.setSuccess(true);
			
			MarkSphere markSphere = new MarkSphere();
			markSphere.setRadius(1);
			markSphere.setPos(position);
			
			
			proposedCfg.getCfgCore().add(markSphere);
			proposedCfg.getCfgToRedraw().add(markSphere);
			proposedCfg.getColoredCfg().add(
				new OverlayMark(markSphere, regionMembership),
				colorMark
			);
			proposedCfg.setSuggestedSliceNum( (int) position.getZ() ); 
			
			return proposedCfg;
		};
	}

}
