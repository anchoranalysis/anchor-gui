/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkProposerEvaluatorNRGStack;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;

public class FromMarkProposer extends ProposalOperationCreatorFromProposer<MarkProposer> {

    public FromMarkProposer() {
        super();
    }

    @Override
    public ProposalOperationCreator creatorFromProposer(MarkProposer proposer) {
        return new MarkProposerEvaluatorNRGStack(proposer, true);
    }

    @Override
    public NamedProvider<MarkProposer> allProposers(MPPInitParams so) {
        return so.getMarkProposerSet();
    }

    @Override
    public String getEvaluatorName() {
        return "Mark Proposer";
    }
}
