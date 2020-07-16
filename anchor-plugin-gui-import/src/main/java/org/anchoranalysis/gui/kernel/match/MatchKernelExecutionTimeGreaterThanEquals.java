/* (C)2020 */
package org.anchoranalysis.gui.kernel.match;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class MatchKernelExecutionTimeGreaterThanEquals extends MatchKernel {

    private long executionTime;

    public MatchKernelExecutionTimeGreaterThanEquals(long executionTime) {
        super();
        this.executionTime = executionTime;
    }

    @Override
    public boolean matches(KernelIterDescription kid) {
        return kid.getExecutionTime() >= executionTime;
    }
}
