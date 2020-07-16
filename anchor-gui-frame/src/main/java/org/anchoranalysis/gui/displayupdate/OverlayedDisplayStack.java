/* (C)2020 */
package org.anchoranalysis.gui.displayupdate;

import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.image.stack.DisplayStack;

public class OverlayedDisplayStack {

    private ColoredOverlayCollection coloredOverlayCollection;
    private DisplayStack displayStack;

    public OverlayedDisplayStack(
            ColoredOverlayCollection coloredOverlayCollection, DisplayStack displayStack) {
        super();
        this.coloredOverlayCollection = coloredOverlayCollection;
        this.displayStack = displayStack;
    }

    public ColoredOverlayCollection getColoredOverlayCollection() {
        return coloredOverlayCollection;
    }

    public DisplayStack getDisplayStack() {
        return displayStack;
    }
}
