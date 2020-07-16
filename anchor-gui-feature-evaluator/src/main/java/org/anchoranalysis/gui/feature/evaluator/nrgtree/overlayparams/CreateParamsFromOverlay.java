/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree.overlayparams;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayMark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.object.OverlayObjectMask;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateIndFromObj;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreatePairFromObj;
import org.anchoranalysis.image.object.ObjectMask;

public class CreateParamsFromOverlay {

    public static CreateFeatureInput<FeatureInput> addForOverlay(
            Overlay overlay, NRGStackWithParams nrgStack, FeatureListWithRegionMap<?> featureList) {
        // TODO replace with object oriented-code
        if (overlay instanceof OverlayMark) {
            OverlayMark overlayCast = (OverlayMark) overlay;

            CreateParamsInd cache = new CreateParamsInd(overlayCast.getMark(), nrgStack);
            return cache.getOrCreate(featureList.get(0).getRegionMap());

        } else if (overlay instanceof OverlayObjectMask) {

            OverlayObjectMask overlayCast = (OverlayObjectMask) overlay;

            return new CreateIndFromObj(overlayCast.getObject().getMask(), nrgStack);

        } else {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static CreateFeatureInput<FeatureInput> addForOverlayPair(
            Pair<Overlay> pair,
            NRGStackWithParams raster,
            FeatureListWithRegionMap<?> featureList) {
        if (pair.getSource() instanceof OverlayMark) {
            assert (pair.getDestination() instanceof OverlayMark);

            Mark source = ((OverlayMark) pair.getSource()).getMark();
            Mark dest = ((OverlayMark) pair.getDestination()).getMark();

            CreateParamsPair cache = new CreateParamsPair(source, dest, raster);
            return cache.getOrCreate(featureList.get(0).getRegionMap());

        } else if (pair.getSource() instanceof OverlayObjectMask) {

            ObjectMask source = ((OverlayObjectMask) pair.getSource()).getObject().getMask();
            ObjectMask dest = ((OverlayObjectMask) pair.getDestination()).getObject().getMask();

            return new CreatePairFromObj(source, dest, raster);
        } else {
            throw new AnchorFriendlyRuntimeException("Unknown type of overlay");
        }
    }
}
