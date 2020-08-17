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

package org.anchoranalysis.gui.image;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.display.BoundColoredOverlayCollection;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.image.stack.DisplayStack;

@RequiredArgsConstructor
public class DisplayUpdateCreator
        implements CheckedFunction<Integer, DisplayUpdate, BackgroundStackContainerException> {

    // START REQUIRED ARGUMENTS
    private final CheckedFunction<
                    Integer, OverlayedDisplayStackUpdate, BackgroundStackContainerException>
            src;

    private final IDGetter<Overlay> idGetter;
    // END REQUIRED ARGUMENTS

    private DrawOverlay drawOverlay;

    // This keeps track of the current over
    private BoundColoredOverlayCollection boundOverlay = null;

    // Must be called before we can bridge any elements
    public void updateDrawer(DrawOverlay drawOverlay) throws SetOperationFailedException {
        this.drawOverlay = drawOverlay;
        if (boundOverlay != null) {
            boundOverlay.updateDrawer(drawOverlay);
        }
    }

    @Override
    public DisplayUpdate apply(Integer sourceObject) throws BackgroundStackContainerException {
        try {
            OverlayedDisplayStackUpdate update = src.apply(sourceObject);

            if (update == null) {
                // No change so we return the existing stack
                return null;
            }

            // System.out.println("OverlayBridge new Element\n");

            // If we haven't a current background yet, then we assume the first update has a
            // BackgroundStack attached
            if (boundOverlay == null) {
                assert (update.getBackgroundStack() != null);
                DisplayStack currentBackground = update.getBackgroundStack();
                boundOverlay =
                        new BoundColoredOverlayCollection(
                                drawOverlay, idGetter, currentBackground.dimensions());
            }

            return update.applyAndCreateDisplayUpdate(boundOverlay);

        } catch (CreateException | OperationFailedException e) {
            throw new BackgroundStackContainerException(e);
        }
    }

    public OverlayRetriever getOverlayRetriever() {
        return boundOverlay.getPrecalculatedCache();
    }
}
