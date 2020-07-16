/* (C)2020 */
package org.anchoranalysis.gui.kernel.match;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class MatchKernelProp extends MatchKernel {

    private boolean proposed;

    public MatchKernelProp(boolean proposed) {
        super();
        this.proposed = proposed;
    }

    @Override
    public boolean matches(KernelIterDescription kid) {
        return kid.isProposed() == proposed;
    }
}
