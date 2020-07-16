/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.object.ObjectMask;

public class CreatePairFromObj implements CreateFeatureInput<FeatureInput> {

    private FeatureInputPairObjects input;

    public CreatePairFromObj(ObjectMask object1, ObjectMask object2, NRGStackWithParams raster) {
        input = new FeatureInputPairObjects(object1, object2, Optional.of(raster));
    }

    @Override
    public FeatureInput createForFeature(Feature<?> feature) throws CreateException {
        return input;
    }
}
