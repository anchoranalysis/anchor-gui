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

package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;
import org.anchoranalysis.image.stack.DisplayStack;

class IndexToRedrawUpdate
        implements CheckedFunction<
                Integer, OverlayedDisplayStack, BackgroundStackContainerException> {

    private BoundedIndexBridge<ColoredOverlayedInstantState> delegate;
    private CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException> background;

    public IndexToRedrawUpdate(
            BoundedIndexContainer<ColoredOverlayedInstantState> cntr,
            CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException> background) {
        delegate = new BoundedIndexBridge<>(cntr);
        this.background = background;
    }

    @Override
    public OverlayedDisplayStack apply(Integer sourceObject)
            throws BackgroundStackContainerException {

        try {
            ColoredOverlayedInstantState found = delegate.apply(sourceObject);

            return new OverlayedDisplayStack(
                    found.getOverlayCollection(), background.apply(sourceObject));

        } catch (GetOperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }

    public void setImageStackCntr(
            CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException>
                    imageStackCntr) {
        this.background = imageStackCntr;
    }
}
