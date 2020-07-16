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
