package org.anchoranalysis.gui.frame.overlays;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;

@FunctionalInterface
public interface IShowEvaluationResult {
	void showEvaluationResult( ProposedCfg er, Cfg bboxRedraw );
}
