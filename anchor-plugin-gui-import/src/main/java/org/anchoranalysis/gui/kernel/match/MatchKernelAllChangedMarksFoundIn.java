/* (C)2020 */
package org.anchoranalysis.gui.kernel.match;

import org.anchoranalysis.core.index.IndicesSelection;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

public class MatchKernelAllChangedMarksFoundIn extends MatchKernel {

    private IndicesSelection markSelection;

    public MatchKernelAllChangedMarksFoundIn(IndicesSelection markSelection) {
        super();
        this.markSelection = markSelection;
    }

    @Override
    public boolean matches(KernelIterDescription kid) {
        return markSelection.allFoundIn(kid.getChangedMarkIDArr());
    }
}
