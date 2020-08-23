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

import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.frame.display.Redrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.GetOverlayCollection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class RedrawFromMarksGetter implements PropertyValueChangeListener<IntArray> {

    // START REQUIRED ARGUMENTS
    // Gives us the currently selected marks
    private final GetOverlayCollection marksGetter;
    private final Redrawable redrawable;
    /// END REQUIRED ARGUMENTS
    
    private ColoredOverlayCollection old;

    @Override
    public synchronized void propertyValueChanged(PropertyValueChangeEvent<IntArray> evt) {

        ColoredOverlayCollection overlays = marksGetter.getOverlays();

        if (old == null) {

            // TODO THIS IS A HACK TO SOLVE, WE CAN MAKE THIS MORE EFFICIENT

            // change to trigger a full redraw
            // and draw with a particular marks

            redrawable.applyRedrawUpdate(
                    OverlayedDisplayStackUpdate.updateChanged(overlays.withoutColor()));

        } else {
            OverlayCollection merged = old.withoutColor().createMerged(overlays.withoutColor());

            assert (merged != null);
            redrawable.applyRedrawUpdate(OverlayedDisplayStackUpdate.updateChanged(merged));
        }

        old = overlays;
    }
}
