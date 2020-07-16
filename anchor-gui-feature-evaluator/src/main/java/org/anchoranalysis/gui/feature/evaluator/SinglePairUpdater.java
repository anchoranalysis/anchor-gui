/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePairList;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;

class SinglePairUpdater {

    private UpdatableSinglePairList updatableMarkPairList = new UpdatableSinglePairList();

    private OverlayDescriptionPanel overlayDescriptionPanel;

    private FinderEvaluator finder;

    public SinglePairUpdater(
            OverlayDescriptionPanel overlayDescriptionPanel,
            FinderEvaluator finder,
            IUpdatableSinglePair secondInitialItem) {
        super();
        this.overlayDescriptionPanel = overlayDescriptionPanel;
        this.finder = finder;
        updatableMarkPairList.add(overlayDescriptionPanel.getMarkDescription());
        updatableMarkPairList.add(secondInitialItem);
    }

    public void updateModel(OverlayCollectionWithImgStack cws) throws CreateException {

        if (overlayDescriptionPanel.isFrozen()) {
            return;
        }

        if (cws != null && cws.getOverlayCollection() != null && cws.getStack() != null) {

            assert (cws.getStack() != null);

            NRGStackWithParams nrgStack = cws.getStack();

            updateOverlays(cws.getOverlayCollection(), nrgStack);

        } else {
            overlayDescriptionPanel.updateDescriptionTop(null);
            updateSingle(null, null);
        }
    }

    private void updateOverlays(OverlayCollection overlays, NRGStackWithParams nrgStack)
            throws CreateException {

        // Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( cws.getOverlayCollection() );
        overlayDescriptionPanel.updateDescriptionTop(overlays);

        assert (nrgStack != null);

        Pair<Overlay> pair = finder.findPairFromCurrentSelection(overlays, nrgStack);

        if (pair != null) {

            // if we have a valid pair, we update with the pair
            updatePair(pair, nrgStack);
        } else {
            Overlay ol = FinderEvaluator.findOverlayFromCurrentSelection(overlays);
            if (ol != null) {
                updateSingle(ol, nrgStack);
            } else {
                overlayDescriptionPanel.updateDescriptionTop(null);
                updateSingle(null, nrgStack);
            }
        }
    }

    private void updateSingle(Overlay overlay, NRGStackWithParams raster) {
        updatableMarkPairList.updateSingle(overlay, raster);
    }

    private void updatePair(Pair<Overlay> pair, NRGStackWithParams raster) {
        updatableMarkPairList.updatePair(pair, raster);
    }
}
