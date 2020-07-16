/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.frame.overlays.RedrawUpdate;

public class RedrawUpdateFromProposal {

    public static RedrawUpdate apply(ProposedCfg er, Cfg bboxRedraw) {
        return new RedrawUpdate(
                selectUpdate(er, bboxRedraw), overlaysForTrigger(er), suggestedSliceNum(er));
    }

    private static OverlayedDisplayStackUpdate selectUpdate(ProposedCfg cfg, Cfg bboxRedraw) {
        ColoredOverlayCollection oc = cfg.getColoredCfg();
        if (bboxRedraw != null) {
            OverlayCollection ocRedraw =
                    OverlayCollectionMarkFactory.createWithoutColor(
                            bboxRedraw, cfg.getRegionMembership());
            return OverlayedDisplayStackUpdate.updateOverlaysWithSimilar(oc, ocRedraw);
        } else {
            return OverlayedDisplayStackUpdate.updateOverlaysWithSimilar(oc);
        }
    }

    // HACK
    // Let's just always send the fist item
    // We create a cfg with just the first item
    private static OverlayCollection overlaysForTrigger(ProposedCfg er) {
        // We the marks back from the overlays
        // Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays(
        // er.getColoredCfg().getOverlayCollection() );

        OverlayCollection ocFirst = new OverlayCollection();

        if (er.isSuccess() && er.getColoredCfg().getOverlays().size() >= 1) {
            ocFirst.add(er.getColoredCfg().getOverlays().get(0));
        }

        return ocFirst;
    }

    private static int suggestedSliceNum(ProposedCfg er) {
        // Slice Nums
        if (er.isSuccess() && er.hasSugestedSliceNum()) {
            return er.getSuggestedSliceNum();
        } else {
            return -1;
        }
    }
}
