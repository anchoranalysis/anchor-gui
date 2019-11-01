package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
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
