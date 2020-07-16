/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.overlay;

import java.util.function.Supplier;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;

// Finds ColoredCfgRedrawUpdate which implement changes to existing ColoredCfg
class FindCorrectUpdate
        implements FunctionWithException<
                Integer, OverlayedDisplayStackUpdate, GetOperationFailedException> {
    private int oldIndex = -1;

    private final FunctionWithException<Integer, OverlayedDisplayStack, GetOperationFailedException>
            integerToOverlayedBridge;

    private Supplier<Boolean> funcHasBeenInit;
    private IGetClearUpdate getClearUpdate;

    public FindCorrectUpdate(
            FunctionWithException<Integer, OverlayedDisplayStack, GetOperationFailedException>
                    integerToOverlayedBridge,
            Supplier<Boolean> funcHasBeenInit,
            IGetClearUpdate getClearUpdate) {
        super();
        this.funcHasBeenInit = funcHasBeenInit;
        this.integerToOverlayedBridge = integerToOverlayedBridge;
        this.getClearUpdate = getClearUpdate;
    }

    @Override
    public OverlayedDisplayStackUpdate apply(Integer sourceObject)
            throws GetOperationFailedException {

        // If our index hasn't changed, then we just apply whatever local updates are queued for
        // processing
        if (funcHasBeenInit.get() && sourceObject.equals(oldIndex)) {
            // System.out.printf("getAndClear\n");
            OverlayedDisplayStackUpdate update = getClearUpdate.getAndClearWaitingUpdate();
            return update;
        } else {
            OverlayedDisplayStack ods = integerToOverlayedBridge.apply(sourceObject);
            oldIndex = sourceObject;

            OverlayedDisplayStackUpdate update =
                    OverlayedDisplayStackUpdate.assignOverlaysAndBackground(
                            ods.getColoredOverlayCollection(), ods.getDisplayStack());

            getClearUpdate.clearWaitingUpdate();
            return update;
        }
    }
}
