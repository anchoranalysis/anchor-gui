/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.evaluator.params.ParamsFactoryForFeature;

@RequiredArgsConstructor
public class CreatePairFromMark implements CreateFeatureInput<FeatureInput> {

    private final VoxelizedMarkMemo pmm1;
    private final VoxelizedMarkMemo pmm2;
    private final NRGStackWithParams raster;

    @Override
    public FeatureInput createForFeature(Feature<?> feature) throws CreateException {
        return ParamsFactoryForFeature.factoryFor(feature).create(pmm1, pmm2, raster);
    }
}
