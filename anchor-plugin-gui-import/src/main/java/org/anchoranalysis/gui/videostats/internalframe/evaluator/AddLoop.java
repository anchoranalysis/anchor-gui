package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;

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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.gui.videostats.internalframe.ProposeLoopPanel;

class AddLoop implements ProposalOperationCreator {
	private ProposalOperationCreator delegate;
	private ProposeLoopPanel loopPanel;
	
	public AddLoop(ProposalOperationCreator delegate, ProposeLoopPanel loopPanel ) {
		super();
		this.delegate = delegate;
		this.loopPanel = loopPanel;
	}

	@Override
	public ProposalOperation create(Cfg cfg, Point3d position,
			ProposerContext context,
			CfgGen cfgGen)
			throws OperationFailedException {

		final ProposalOperation po = delegate.create(cfg, position, context, cfgGen);
		
		return new ProposalOperation() {
			
			@Override
			public ProposedCfg propose(ErrorNode errorNode) throws ProposalAbnormalFailureException {
				return loopPanel.propose(po, errorNode);
			}
		};
	}
}
