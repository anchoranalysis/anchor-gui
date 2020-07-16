/* (C)2020 */
package org.anchoranalysis.gui.frame.overlays.onrgb;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;

class CfgCntrBridge
        implements FunctionWithException<
                Integer, OverlayedDisplayStack, GetOperationFailedException> {

    private SingleContainer<OverlayedDisplayStack> cfgCntr;

    public CfgCntrBridge(SingleContainer<OverlayedDisplayStack> cfgCntr) {
        super();
        this.cfgCntr = cfgCntr;
    }

    @Override
    public OverlayedDisplayStack apply(Integer sourceObject) throws GetOperationFailedException {
        return cfgCntr.get(0);
    }
}
