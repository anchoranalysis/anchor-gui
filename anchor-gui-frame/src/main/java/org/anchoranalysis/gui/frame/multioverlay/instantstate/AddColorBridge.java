/* (C)2020 */
package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;

class AddColorBridge
        implements FunctionWithException<
                OverlayedInstantState, ColoredOverlayedInstantState, AnchorNeverOccursException> {

    private ColorIndex colorIndex;
    private IDGetter<Overlay> colorIDGetter;

    public AddColorBridge(ColorIndex colorIndex, IDGetter<Overlay> colorIDGetter) {
        super();
        this.colorIndex = colorIndex;
        this.colorIDGetter = colorIDGetter;
    }

    @Override
    public ColoredOverlayedInstantState apply(OverlayedInstantState sourceObject) {

        OverlayCollection oc = sourceObject.getOverlayCollection();

        ColoredOverlayCollection coc =
                new ColoredOverlayCollection(oc, createColorListForOverlays(oc));

        return new ColoredOverlayedInstantState(sourceObject.getIndex(), coc);
    }

    private ColorList createColorListForOverlays(OverlayCollection oc) {

        ColorList colorList = new ColorList();

        for (int i = 0; i < oc.size(); i++) {
            Overlay ol = oc.get(i);
            colorList.add(colorIndex.get(colorIDGetter.getID(ol, i)));
        }

        return colorList;
    }
}
