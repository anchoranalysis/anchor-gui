package org.anchoranalysis.gui.videostats.internalframe.markredraw;

import org.anchoranalysis.anchor.mpp.mark.Mark;

/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;

import anchor.provider.bean.ProposalAbnormalFailureException;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.cfg.CfgGen;
import ch.ethz.biol.cell.mpp.cfg.proposer.CfgProposer;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.evaluator.EvaluatorUtilities;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;

public class CfgProposerEvaluator implements ProposalOperationCreator {

	private CfgProposer proposer;
	
	public CfgProposerEvaluator(CfgProposer cfgProposer) {
		super();
		this.proposer = cfgProposer;
		assert( proposer!=null );
	}

	private static ColoredCfg generateOutputCfg( Cfg cfg ) {
		
		if (cfg==null) {
			return new ColoredCfg();
		}
		
		Cfg cfgNew = cfg.deepCopy();
		
		// We replace all the IDs with 0
		for (Mark m : cfgNew) {
			m.setId(0);
		}
		
		return new ColoredCfg(cfgNew, EvaluatorUtilities.createDefaultColorList() );
	}

	@Override
	public ProposalOperation create(
		Cfg cfg,
		Point3d position,
		ProposerContext context,
		final CfgGen cfgGen
	) {
		// We actually do the proposal
		
		// A holder
		
		// Do proposal
		ProposalOperation doProposal = new ProposalOperation() {
			
			@Override
			public ProposedCfg propose(ErrorNode errorNode) throws ProposalAbnormalFailureException {
				
				ProposedCfg er = new ProposedCfg();

				// TODO replace proposer
				Cfg cfg = proposer.propose( cfgGen, context.replaceError(errorNode) );
				er.setSuccess(cfg!=null);
				
				ColoredCfg coloredCfg = generateOutputCfg( cfg );
				er.setColoredCfg( coloredCfg );
				er.setCfgToRedraw( cfg.createMerged(coloredCfg.getCfg()) );
				er.setCfgCore(cfg);
				
				if (cfg!=null) {
					er.setSuggestedSliceNum( (int) cfg.get(0).centerPoint().getZ() );
				}
				return er;
			}
		};
	
		
		//lastMark = m;
		return doProposal;
	}
}
