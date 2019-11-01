package org.anchoranalysis.gui.videostats.internalframe.evaluator;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.awt.Color;

import org.anchoranalysis.anchor.mpp.mark.conic.MarkSphere;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.extent.ImageDim;

import overlay.OverlayMark;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.cfg.CfgGen;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public class MarkSphereOnPointProposerEvaluator implements ProposalOperationCreator {

	private RGBColor colorMark = new RGBColor(Color.YELLOW);
	
	private ImageDim dim;
	
	private static RegionMembershipWithFlags regionMembership = RegionMapSingleton.instance().membershipWithFlagsForIndex(
		GlobalRegionIdentifiers.SUBMARK_INSIDE
	);
	
	public MarkSphereOnPointProposerEvaluator(ImageDim dim) {
		super();
		this.dim = dim;
	}



	@Override
	public ProposalOperation create(Cfg cfg, final Point3d position,
			ProposerContext context,
			CfgGen cfgGen)
			throws OperationFailedException {
		
		return new ProposalOperation() {

			@Override
			public ProposedCfg propose(ErrorNode errorNode) {

				
				ProposedCfg proposedCfg = new ProposedCfg();
				proposedCfg.setDimensions( dim );
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
			}
			
		};
	}

}
