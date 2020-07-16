/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkMergeProposerEvaluator;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;

public class FromMarkMergeProposer extends ProposalOperationCreatorFromProposer<MarkMergeProposer> {

    @Override
    public ProposalOperationCreator creatorFromProposer(MarkMergeProposer proposer) {
        return new MarkMergeProposerEvaluator(proposer);
    }

    @Override
    public NamedProvider<MarkMergeProposer> allProposers(MPPInitParams so) {
        return so.getMarkMergeProposerSet();
    }

    @Override
    public String getEvaluatorName() {
        return "Merge Proposer";
    }
}
