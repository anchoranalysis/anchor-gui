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

package org.anchoranalysis.gui.frame.overlays.onrgb;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.image.stack.DisplayStack;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class BackgroundSetterLocal implements IBackgroundSetter {

    private IRedrawable redrawable;

    @Override
    public void setImageStackCntr(
            FunctionWithException<Integer, DisplayStack, BackgroundStackContainerException>
                    imageStackCntr)
            throws SetOperationFailedException {

        DisplayStack stack;
        try {
            stack = imageStackCntr.apply(0);
        } catch (BackgroundStackContainerException e) {
            throw new SetOperationFailedException(e);
        }

        redrawable.applyRedrawUpdate(OverlayedDisplayStackUpdate.assignBackground(stack));
    }
}
