/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class MemoPairwiseFactory extends PairwiseFactory {

    @Override
    public FeatureInput create(
            VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, NRGStackWithParams raster)
            throws CreateException {
        return new FeatureInputPairMemo(pmm1, pmm2, raster);
    }
}
