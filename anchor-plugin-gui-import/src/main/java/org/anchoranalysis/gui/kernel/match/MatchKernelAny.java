/* (C)2020 */
package org.anchoranalysis.gui.kernel.match;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class MatchKernelAny extends MatchKernel {

    private int kernelID;

    public MatchKernelAny(int kernelID) {
        super();
        this.kernelID = kernelID;
    }

    @Override
    public boolean matches(KernelIterDescription kid) {

        // Special case to disable kernelID matching
        if (kernelID == -1) {
            return true;
        }

        return kid.getId() == kernelID;
    }
}
