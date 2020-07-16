/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.cfgtorgb;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.index.SingleIndexCntr;

@EqualsAndHashCode(callSuper = true)
public class ColoredOverlayedInstantState extends SingleIndexCntr {

    private final ColoredOverlayCollection coloredOverlayCollection;

    public ColoredOverlayedInstantState(
            int iter, ColoredOverlayCollection coloredOverlayCollection) {
        super(iter);
        this.coloredOverlayCollection = coloredOverlayCollection;
    }

    public ColoredOverlayCollection getOverlayCollection() {
        return coloredOverlayCollection;
    }
}
