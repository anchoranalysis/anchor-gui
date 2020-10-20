/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.frame.threaded.overlay;

import java.util.function.Supplier;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;

// Finds ColoredMarksRedrawUpdate which implement changes to existing ColoredMarks
class FindCorrectUpdate
        implements CheckedFunction<
                Integer, OverlayedDisplayStackUpdate, BackgroundStackContainerException> {
    private int oldIndex = -1;

    private final CheckedFunction<Integer, OverlayedDisplayStack, BackgroundStackContainerException>
            integerToOverlayedBridge;

    private Supplier<Boolean> funcHasBeenInit;
    private IGetClearUpdate getClearUpdate;

    public FindCorrectUpdate(
            CheckedFunction<Integer, OverlayedDisplayStack, BackgroundStackContainerException>
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
            throws BackgroundStackContainerException {

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
