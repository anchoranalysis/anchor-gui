/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkSplitProposerEvaluator;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;

public class FromMarkSplitProposer extends ProposalOperationCreatorFromProposer<MarkSplitProposer> {

    @Override
    public ProposalOperationCreator creatorFromProposer(MarkSplitProposer proposer) {
        return new MarkSplitProposerEvaluator(proposer);
    }

    @Override
    public NamedProvider<MarkSplitProposer> allProposers(MPPInitParams so) {
        return so.getMarkSplitProposerSet();
    }

    @Override
    public String getEvaluatorName() {
        return "Split Proposer";
    }
}
