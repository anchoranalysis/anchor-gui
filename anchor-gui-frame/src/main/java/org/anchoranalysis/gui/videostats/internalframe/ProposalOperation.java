/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe;

import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;

public interface ProposalOperation {

    ProposedCfg propose(ErrorNode errorNode) throws ProposalAbnormalFailureException;
}
