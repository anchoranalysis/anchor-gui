package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

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


import overlay.OverlayCollectionMarkFactory;

import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IAcceptProposal;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IReplaceRemove;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

public class GuessTool extends AnnotationTool {

	private IReplaceRemove replaceRemove;
	private IAcceptProposal acceptProposal;
	private EvaluatorWithContext evaluatorWithContext;
	private ToolErrorReporter toolErrorReporter;
	
	public GuessTool(
		IReplaceRemove replaceRemove,
		IAcceptProposal acceptProposal,
		EvaluatorWithContext evaluatorWithContext,
		ToolErrorReporter toolErrorReporter
	) {
		super();
		this.replaceRemove = replaceRemove;
		this.evaluatorWithContext = evaluatorWithContext;
		this.toolErrorReporter = toolErrorReporter;
		this.acceptProposal = acceptProposal;
	}
	
	public boolean isEnabled() {
		return evaluatorWithContext!=null;
	}

	@Override
	public void proposed(ProposedCfg proposedCfg) {
		
		if (!isEnabled()) {
			return;
		}
		
		if (proposedCfg.isSuccess()) {
			replaceRemove.replaceCurrentProposedCfg(
				proposedCfg.getCfgCore(),
				OverlayCollectionMarkFactory.cfgFromOverlays(proposedCfg.getColoredCfg()),
				proposedCfg.getSuggestedSliceNum()
			);
		} else {
			replaceRemove.removeCurrentProposedCfg();
			toolErrorReporter.showError(
				GuessTool.class,
				"Guess failed. Try again (or select points)!",
				proposedCfg.getPfd().describe()				
			);
		}		
	}

	@Override
	public void confirm(boolean accepted) {

		if (!isEnabled()) {
			return;
		}

		acceptProposal.confirm(accepted);
	}

	@Override
	public EvaluatorWithContext evaluatorWithContextGetter() {
		return evaluatorWithContext;
	}

	@Override
	public void leftMouseClickedAtPoint(Point3d pnt) {
	}

}
