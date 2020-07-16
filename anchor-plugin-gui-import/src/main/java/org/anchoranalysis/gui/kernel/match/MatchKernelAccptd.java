/* (C)2020 */
package org.anchoranalysis.gui.kernel.match;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class MatchKernelAccptd extends MatchKernel {

    private boolean accepted;

    public MatchKernelAccptd(boolean accepted) {
        super();
        this.accepted = accepted;
    }

    @Override
    public boolean matches(KernelIterDescription kid) {
        return kid.isProposed() && kid.isAccepted() == accepted;
    }
}
