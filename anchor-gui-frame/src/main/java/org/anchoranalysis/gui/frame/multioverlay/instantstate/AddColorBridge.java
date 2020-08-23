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

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.IndexableOverlays;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.IndexableColoredOverlays;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class AddColorBridge
        implements CheckedFunction<
                IndexableOverlays, IndexableColoredOverlays, AnchorNeverOccursException> {

    private final ColorIndex colorIndex;
    private final IDGetter<Overlay> colorIDGetter;

    @Override
    public IndexableColoredOverlays apply(IndexableOverlays sourceObject) {

        OverlayCollection overlays = sourceObject.getOverlays();

        ColoredOverlayCollection coloredOverlays =
                new ColoredOverlayCollection(overlays, createColorListForOverlays(overlays));

        return new IndexableColoredOverlays(sourceObject.getIndex(), coloredOverlays);
    }

    private ColorList createColorListForOverlays(OverlayCollection overlays) {

        ColorList colors = new ColorList();

        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            colors.add(colorIndex.get(colorIDGetter.getID(overlay, i)));
        }

        return colors;
    }
}
