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

package org.anchoranalysis.gui.feature.evaluator.energytree.overlayparams;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.energytree.createparams.CreateIndFromObj;
import org.anchoranalysis.gui.feature.evaluator.energytree.createparams.CreatePairFromObj;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.overlay.OverlayMark;
import org.anchoranalysis.mpp.pair.IdentifiablePair;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.object.OverlayObjectMask;

public class CreateParamsFromOverlay {

    public static CreateFeatureInput<FeatureInput> addForOverlay(
            Overlay overlay, EnergyStack energyStack, FeatureListWithRegionMap<?> featureList) {
        // TODO replace with object oriented-code
        if (overlay instanceof OverlayMark) {
            OverlayMark overlayCast = (OverlayMark) overlay;

            CreateParamsInd cache = new CreateParamsInd(overlayCast.getMark(), energyStack);
            return cache.getOrCreate(featureList.get(0).getRegionMap());

        } else if (overlay instanceof OverlayObjectMask) {

            OverlayObjectMask overlayCast = (OverlayObjectMask) overlay;

            return new CreateIndFromObj(overlayCast.getObject().withoutProperties(), energyStack);

        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static CreateFeatureInput<FeatureInput> addForOverlayPair(
            IdentifiablePair<Overlay> pair,
            EnergyStack raster,
            FeatureListWithRegionMap<?> featureList) {
        if (pair.getSource() instanceof OverlayMark) {
            assert (pair.getDestination() instanceof OverlayMark);

            Mark source = ((OverlayMark) pair.getSource()).getMark();
            Mark dest = ((OverlayMark) pair.getDestination()).getMark();

            CreateParamsPair cache = new CreateParamsPair(source, dest, raster);
            return cache.getOrCreate(featureList.get(0).getRegionMap());

        } else if (pair.getSource() instanceof OverlayObjectMask) {

            ObjectMask source =
                    ((OverlayObjectMask) pair.getSource()).getObject().withoutProperties();
            ObjectMask dest =
                    ((OverlayObjectMask) pair.getDestination()).getObject().withoutProperties();

            return new CreatePairFromObj(source, dest, raster);
        } else {
            throw new AnchorFriendlyRuntimeException("Unknown type of overlay");
        }
    }
}
