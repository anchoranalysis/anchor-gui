/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.evaluator.params.ParamsFactoryForFeature;

public class CreateIndFromMark implements CreateFeatureInput<FeatureInput> {

    private VoxelizedMarkMemo pmm;
    private NRGStackWithParams raster;

    public CreateIndFromMark(VoxelizedMarkMemo pmm, NRGStackWithParams raster) {
        super();
        this.pmm = pmm;
        this.raster = raster;
    }

    @Override
    public FeatureInput createForFeature(Feature<?> feature) throws CreateException {
        return ParamsFactoryForFeature.factoryFor(feature).create(pmm, raster);
    }
}
