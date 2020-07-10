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
import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.PairPxlMarkMemo;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;

public class MarkSplitProposerEvaluator implements ProposalOperationCreator {

	private final MarkSplitProposer markSplitProposer;
	
	private Optional<PairPxlMarkMemo> pair = Optional.empty();
	private Mark exstMark;
	@SuppressWarnings("unused")
	private Cfg exstCfg;
	
	public MarkSplitProposerEvaluator(MarkSplitProposer markSplitProposer) {
		super();		
		assert(markSplitProposer!=null);
		
		this.markSplitProposer = markSplitProposer;
	}

	@Override
	public ProposalOperation create(final Cfg cfg,
			Point3d position,
			final ProposerContext context,
			final CfgGen cfgGen
		) throws OperationFailedException {
		
		this.exstCfg = cfg;
		
		// We need to get the mark already at this position
		final Cfg marksAtPost = cfg.marksAt( position, context.getRegionMap(), GlobalRegionIdentifiers.SUBMARK_INSIDE );
		
		return new ProposalOperation() {
			
			@Override
			public ProposedCfg propose(ErrorNode errorNode) throws ProposalAbnormalFailureException {
		
				if (marksAtPost.size()==0) {
					errorNode.add("no existing mark found");
					return new ProposedCfg();
				}
				
				if (marksAtPost.size()>1) {
					errorNode.add("more than one existing mark found");
					return new ProposedCfg();
				}
				
				exstMark = marksAtPost.get(0);
				
				{
					VoxelizedMarkMemo pmmExstMark = context.create(exstMark);
					pair = markSplitProposer.propose(pmmExstMark, context, cfgGen);
				}
				
				if (pair.isPresent()) {
					ProposedCfg er = new ProposedCfg();
					er.setDimensions( context.getDimensions() );
					er.setSuccess(true);
					er.setColoredCfg(cfgForLast());
					er.setCfgToRedraw(cfg);
					
					Cfg core = new Cfg();
					core.add( pair.get().getSource().getMark() );
					core.add( pair.get().getDestination().getMark() );
					er.setCfgCore( core );
					return er;
				} else {
					ProposedCfg er = new ProposedCfg();
					er.setCfgToRedraw(cfg);
					return er;
				}
			}
		};
	}

	private ColoredCfg cfgForLast() {
		ColoredCfg cfgOut = new ColoredCfg();
		if (pair.isPresent()) {
			// We change the IDs
			cfgOut.addChangeID( pair.get().getSource().getMark(), new RGBColor(Color.BLUE) );
			cfgOut.addChangeID( pair.get().getDestination().getMark(), new RGBColor(Color.RED) );
		}
		return cfgOut;
	}

}
