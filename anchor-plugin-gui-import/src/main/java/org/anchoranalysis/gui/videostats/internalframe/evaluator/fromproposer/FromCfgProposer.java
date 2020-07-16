/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.proposer.CfgProposer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;
import org.anchoranalysis.gui.videostats.internalframe.markredraw.CfgProposerEvaluator;

public class FromCfgProposer extends ProposalOperationCreatorFromProposer<CfgProposer> {

    @Override
    public ProposalOperationCreator creatorFromProposer(CfgProposer proposer) {
        return new CfgProposerEvaluator(proposer);
    }

    @Override
    public NamedProvider<CfgProposer> allProposers(MPPInitParams so) {
        return so.getCfgProposerSet();
    }

    @Override
    public String getEvaluatorName() {
        return "Cfg Proposer";
    }
}
