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
import java.util.List;

import org.anchoranalysis.anchor.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.cfg.CfgGen;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.evaluator.EvaluatorUtilities;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;

public class MarkMergeProposerEvaluator implements ProposalOperationCreator {

	private final MarkMergeProposer markMergeProposer;
	
	public MarkMergeProposerEvaluator(MarkMergeProposer markMergeProposer) {
		super();		
		assert(markMergeProposer!=null);
		
		this.markMergeProposer = markMergeProposer;
	}

	@Override
	public ProposalOperation create(final Cfg cfg, Point3d position,
			final ProposerContext context, final CfgGen cfgGen) throws OperationFailedException {
		
		if (cfg.size()!=2) {
			throw new IllegalArgumentException("The existing configuration must have exactly 2 items");
		}

		return new ProposalOperation() {
			@Override
			public ProposedCfg propose(ErrorNode errorNode) throws ProposalAbnormalFailureException {

				Mark mark1 = cfg.get(0);
				Mark mark2 = cfg.get(1);
				
				PxlMarkMemo markMemo1 = context.create( mark1 );
				PxlMarkMemo markMemo2 = context.create( mark2 );
				
				Mark proposedMark = markMergeProposer.propose( markMemo1, markMemo2, context.replaceError(errorNode) );
				
				ProposedCfg er = new ProposedCfg();
				er.setDimensions( context.getDimensions() );
				
				if (proposedMark!=null) {
					er.setSuccess( true );
					
					ColoredCfg coloredCfg = cfgForMark(proposedMark);
					er.setColoredCfg( coloredCfg );	
					er.setCfgToRedraw( cfg.createMerged( coloredCfg.getCfg()) );
					er.setCfgCore( new Cfg(proposedMark) );
				} else {
					er.setCfgToRedraw( cfg );
				}
				return er;
				
			}
		};

	}

	private ColoredCfg cfgForMark( Mark mark) {
		
		ColoredCfg cfgOut = new ColoredCfg();
		if (mark!=null) {
		
			Mark markNew = mark.duplicate();
			markNew.setId(0);
			
			cfgOut.addChangeID( markNew, new RGBColor( Color.BLUE)  );
		}
	
		
		// Allows us to associate a list of points with  the mark
		{
			List<Point3f> pts = markMergeProposer.getLastPnts1();
			if (pts!=null) {
				cfgOut.addChangeID( EvaluatorUtilities.createMarkFromPoints3f(pts,false), new RGBColor( Color.GREEN) );	// 1 is just to give us a different color
			}
		}
		
		// Allows us to associate a list of points with  the mark
		{
			List<Point3f> pts = markMergeProposer.getLastPnts2();
			if (pts!=null) {
				cfgOut.addChangeID( EvaluatorUtilities.createMarkFromPoints3f(pts,false), new RGBColor( Color.YELLOW) );	// 1 is just to give us a different color
			}
		}
		
		return cfgOut;
	}

}
