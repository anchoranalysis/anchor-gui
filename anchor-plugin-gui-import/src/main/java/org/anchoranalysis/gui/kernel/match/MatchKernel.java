/* (C)2020 */
package org.anchoranalysis.gui.kernel.match;

import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public abstract class MatchKernel {

    public abstract boolean matches(KernelIterDescription kid);
}
