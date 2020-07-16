/*-
 * #%L
 * anchor-gui-common
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
/* (C)2020 */
package org.anchoranalysis.gui.frame.display;

import java.util.List;
import org.anchoranalysis.image.extent.BoundingBox;

public class DisplayUpdate {

    /** What parts of the OverlayedDisplayStack to redraw. If null, everything is redrawn. */
    private List<BoundingBox> redrawParts;

    /**
     * A display-stack with some sort of overlay on top of it. If non-null, it indicates a new
     * OverlayedDisplayStack has been assigned. If null, then the existing remains true.
     */
    private BoundOverlayedDisplayStack displayStack;

    private DisplayUpdate(BoundOverlayedDisplayStack displayStack, List<BoundingBox> redrawParts) {
        super();
        this.redrawParts = redrawParts;
        this.displayStack = displayStack;
    }

    public static DisplayUpdate assignNewStack(BoundOverlayedDisplayStack displayStack) {
        return new DisplayUpdate(displayStack, null);
    }

    public static DisplayUpdate redrawParts(List<BoundingBox> list) {
        return new DisplayUpdate(null, list);
    }

    public static DisplayUpdate redrawEverything() {
        return new DisplayUpdate(null, null);
    }

    // Note that the bounding boxes are always in the original image coordinates, not the
    //   coordinates of the zoomed displays
    public List<BoundingBox> getRedrawParts() {
        return redrawParts;
    }

    public BoundOverlayedDisplayStack getDisplayStack() {
        return displayStack;
    }
}
