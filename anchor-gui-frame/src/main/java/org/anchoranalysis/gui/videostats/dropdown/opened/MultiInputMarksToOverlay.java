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

package org.anchoranalysis.gui.videostats.dropdown.opened;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.BridgeElementWithIndex;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.MultiInput;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.IndexableOverlays;
import org.anchoranalysis.overlay.collection.OverlayCollection;

@RequiredArgsConstructor
class MultiInputMarksToOverlay
        implements BridgeElementWithIndex<
                MultiInput<MarkCollection>, IndexableOverlays, OperationFailedException> {

    private final MarkDisplaySettings markDisplaySettings;

    @Override
    public IndexableOverlays bridgeElement(int index, MultiInput<MarkCollection> sourceObject)
            throws OperationFailedException {

        MarkCollection marks = sourceObject.getAssociatedObjects().get();

        OverlayCollection overlays =
                OverlayCollectionMarkFactory.createWithoutColor(
                        marks, markDisplaySettings.regionMembership());
        return new IndexableOverlays(index, overlays);
    }
}
