/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
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

    public AddLoop(ProposalOperationCreator delegate, ProposeLoopPanel loopPanel) {
        super();
        this.delegate = delegate;
        this.loopPanel = loopPanel;
    }

    @Override
    public ProposalOperation create(
            Cfg cfg, Point3d position, ProposerContext context, CfgGen cfgGen)
            throws OperationFailedException {

        final ProposalOperation po = delegate.create(cfg, position, context, cfgGen);

        return new ProposalOperation() {

            @Override
            public ProposedCfg propose(ErrorNode errorNode)
                    throws ProposalAbnormalFailureException {
                return loopPanel.propose(po, errorNode);
            }
        };
    }
}
