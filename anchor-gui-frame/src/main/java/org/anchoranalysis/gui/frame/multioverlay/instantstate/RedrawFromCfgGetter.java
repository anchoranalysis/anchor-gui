/* (C)2020 */
package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.GetOverlayCollection;

class RedrawFromCfgGetter implements PropertyValueChangeListener<IntArray> {

    // Gives us the currently selected marks
    private GetOverlayCollection cfgGetter;
    private IRedrawable redrawable;
    private ColoredOverlayCollection old;

    public RedrawFromCfgGetter(
            GetOverlayCollection cfgGetter, IRedrawable redrawable, Logger logger) {
        super();
        assert (cfgGetter != null);
        assert (redrawable != null);
        this.cfgGetter = cfgGetter;
        this.redrawable = redrawable;
    }

    @Override
    public synchronized void propertyValueChanged(PropertyValueChangeEvent<IntArray> evt) {

        ColoredOverlayCollection cfgNew = cfgGetter.getOverlays();

        if (old == null) {

            // TODO THIS IS A HACK TO SOLVE, WE CAN MAKE THIS MORE EFFICIENT

            // change to trigger a full redraw
            // and draw with a particular cfg
            // redrawable.redrawAll();

            redrawable.applyRedrawUpdate(
                    OverlayedDisplayStackUpdate.updateChanged(cfgNew.withoutColor()));

            // redrawable.redraw(cfg)

            // cfgGenerator.redraw( cfgNew );
            // cfgGenerator.generate();
            // cfgGenerator.redrawAll();
        } else {
            OverlayCollection merged = old.withoutColor().createMerged(cfgNew.withoutColor());
            // redrawable.redrawParts(  );

            assert (merged != null);
            redrawable.applyRedrawUpdate(OverlayedDisplayStackUpdate.updateChanged(merged));

            // cnvtr.update(old, bboxList);

        }

        old = cfgNew;
    }
}
