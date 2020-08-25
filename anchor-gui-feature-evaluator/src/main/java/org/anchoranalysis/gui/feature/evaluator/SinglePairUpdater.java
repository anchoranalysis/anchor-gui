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

import org.anchoranalysis.anchor.mpp.pair.IdentifiablePair;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePair;
import org.anchoranalysis.gui.feature.evaluator.singlepair.UpdatableSinglePairList;
import org.anchoranalysis.gui.image.OverlaysWithEnergyStack;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.OverlayCollection;

class SinglePairUpdater {

    private UpdatableSinglePairList updatableMarkPairList = new UpdatableSinglePairList();

    private OverlayDescriptionPanel overlayDescriptionPanel;

    private FinderEvaluator finder;

    public SinglePairUpdater(
            OverlayDescriptionPanel overlayDescriptionPanel,
            FinderEvaluator finder,
            UpdatableSinglePair secondInitialItem) {
        super();
        this.overlayDescriptionPanel = overlayDescriptionPanel;
        this.finder = finder;
        updatableMarkPairList.add(overlayDescriptionPanel.getMarkDescription());
        updatableMarkPairList.add(secondInitialItem);
    }

    public void updateModel(OverlaysWithEnergyStack cws) throws CreateException {

        if (overlayDescriptionPanel.isFrozen()) {
            return;
        }

        if (cws != null && cws.getOverlays() != null && cws.getStack() != null) {

            assert (cws.getStack() != null);

            EnergyStack energyStack = cws.getStack();

            updateOverlays(cws.getOverlays(), energyStack);

        } else {
            overlayDescriptionPanel.updateDescriptionTop(null);
            updateSingle(null, null);
        }
    }

    private void updateOverlays(OverlayCollection overlays, EnergyStack energyStack)
            throws CreateException {

        // Marks marks = OverlayCollectionMarkFactory.marksFromOverlays( cws.getOverlayCollection() );
        overlayDescriptionPanel.updateDescriptionTop(overlays);

        assert (energyStack != null);

        IdentifiablePair<Overlay> pair = finder.findPairFromCurrentSelection(overlays, energyStack);

        if (pair != null) {

            // if we have a valid pair, we update with the pair
            updatePair(pair, energyStack);
        } else {
            Overlay ol = FinderEvaluator.findOverlayFromCurrentSelection(overlays);
            if (ol != null) {
                updateSingle(ol, energyStack);
            } else {
                overlayDescriptionPanel.updateDescriptionTop(null);
                updateSingle(null, energyStack);
            }
        }
    }

    private void updateSingle(Overlay overlay, EnergyStack raster) {
        updatableMarkPairList.updateSingle(overlay, raster);
    }

    private void updatePair(IdentifiablePair<Overlay> pair, EnergyStack raster) {
        updatableMarkPairList.updatePair(pair, raster);
    }
}
