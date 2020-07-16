/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer;

import java.util.Set;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;

public abstract class ProposalOperationCreatorFromProposer<T> {
    private NamedProvider<T> set;

    public ProposalOperationCreatorFromProposer() {
        super();
    }

    public void init(MPPInitParams so) {
        this.set = allProposers(so);
    }

    public Set<String> keys() {
        return set.keys();
    }

    private T getItem(String itemName) throws NamedProviderGetException {
        return set.getException(itemName);
    }

    public ProposalOperationCreator createEvaluator(String itemName) throws CreateException {
        try {
            T proposer = getItem(itemName);
            return creatorFromProposer(proposer);
        } catch (NamedProviderGetException e) {
            throw new CreateException(e);
        }
    }

    public abstract ProposalOperationCreator creatorFromProposer(T proposer);

    public abstract NamedProvider<T> allProposers(MPPInitParams so);

    public abstract String getEvaluatorName();
}
