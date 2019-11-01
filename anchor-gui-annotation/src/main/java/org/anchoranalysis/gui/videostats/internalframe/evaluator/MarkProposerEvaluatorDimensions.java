package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;

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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.extent.ImageDim;

import anchor.provider.bean.ProposalAbnormalFailureException;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.cfg.CfgGen;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemo;
import ch.ethz.biol.cell.mpp.mark.pxlmark.memo.PxlMarkMemoFactory;

public class MarkProposerEvaluatorDimensions implements ProposalOperationCreator {

	private MarkProposer markProposer;
	private ImageDim dim;
	private boolean detailedVisualization;
	
	public MarkProposerEvaluatorDimensions(MarkProposer markProposer, boolean detailedVisualization) {
		super();
		this.markProposer = markProposer;
		assert( markProposer.isHasBeenInit() );
		this.detailedVisualization = detailedVisualization;
	}
	

	@Override
	public ProposalOperation create( Cfg cfg, final Point3d position, final ProposerContext context, final CfgGen cfgGen ) throws OperationFailedException {

		// We actually do the proposal
		
		// Do proposal
		ProposalOperation doProposal = new ProposalOperation() {
			
			final Mark m = MarkProposerEvaluatorUtilities.createMarkFromPosition( position, cfgGen.getTemplateMark().create(), context.getDimensions(), context.getRe());
			
			@Override
			public ProposedCfg propose(ErrorNode errorNode) throws ProposalAbnormalFailureException {

				PxlMarkMemo pmm = PxlMarkMemoFactory.create( m, null, context.getRegionMap() );
				
				ProposedCfg er = new ProposedCfg();
				er.setDimensions( dim );
				
				assert( markProposer.isHasBeenInit() );
				
				// assumes only called once
				boolean succ = markProposer.propose( pmm, context );
				
				er.setSuccess(succ);
				
				if (succ) {
					er.setColoredCfg( MarkProposerEvaluatorUtilities.generateCfgFromMark( pmm.getMark(), position, markProposer, detailedVisualization ) );
					er.setSuggestedSliceNum( (int) m.centerPoint().getZ() );
					er.setCfgCore( new Cfg(pmm.getMark()) );
				}
				
				return er;
			}
		};
		return doProposal;

	}

	
	
}