/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator;

import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePairList;
import org.anchoranalysis.gui.image.OverlayCollectionWithNrgStack;

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

    public void updateModel(OverlayCollectionWithNrgStack cws) throws CreateException {

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
