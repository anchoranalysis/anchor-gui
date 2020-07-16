/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;

public class CreateIndFromObj implements CreateFeatureInput<FeatureInput> {

    private FeatureInputSingleObject input;

    public CreateIndFromObj(ObjectMask object, NRGStackWithParams nrgStack) {
        super();
        input = new FeatureInputSingleObject(object, nrgStack);
    }

    @Override
    public FeatureInput createForFeature(Feature<?> feature) throws CreateException {
        return input;
    }
}
