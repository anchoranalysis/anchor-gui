/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

// A MarkEvaluator after it has been resolved for usage by converting
//  it into a ProposerSharedObjectsImageSpecific and other necessary components
public class MarkEvaluatorResolved {

    private final OperationInitParams operationCreateProposerSharedObjects;
    private final OperationNrgStack operationCreateNrgStack;
    private final CfgGen cfgGen;
    private final NRGScheme nrgScheme;

    public MarkEvaluatorResolved(
            OperationInitParams proposerSharedObjects,
            CfgGen cfgGen,
            NRGScheme nrgScheme,
            KeyValueParams params) {
        super();
        this.operationCreateProposerSharedObjects = proposerSharedObjects;
        this.cfgGen = cfgGen;
        this.nrgScheme = nrgScheme;

        this.operationCreateNrgStack =
                new OperationNrgStack(operationCreateProposerSharedObjects, params);
    }

    public OperationInitParams getProposerSharedObjectsOperation() {
        return operationCreateProposerSharedObjects;
    }

    public NRGStackWithParams getNRGStack() throws GetOperationFailedException {
        try {
            return operationCreateNrgStack.doOperation();
        } catch (CreateException e) {
            throw new GetOperationFailedException(e);
        }
    }

    public CfgGen getCfgGen() {
        return cfgGen;
    }

    public NRGScheme getNrgScheme() {
        return nrgScheme;
    }
}
