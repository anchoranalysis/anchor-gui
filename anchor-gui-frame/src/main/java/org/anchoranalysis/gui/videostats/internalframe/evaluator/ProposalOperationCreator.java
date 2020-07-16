/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;

public interface ProposalOperationCreator {

    ProposalOperation create(Cfg cfg, Point3d position, ProposerContext context, CfgGen cfgGen)
            throws OperationFailedException;
}
