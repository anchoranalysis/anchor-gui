/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.input.FeatureInputNull;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class NullFactory implements FeatureInputFactory {

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams raster)
            throws CreateException {
        return FeatureInputNull.instance();
    }

    @Override
    public FeatureInput create(
            VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, NRGStackWithParams raster)
            throws CreateException {
        return FeatureInputNull.instance();
    }

    @Override
    public FeatureInput create(MemoCollection pmmhList, NRGStackWithParams raster)
            throws CreateException {
        return FeatureInputNull.instance();
    }

    @Override
    public boolean isPairwiseSupported() {
        return true;
    }

    @Override
    public boolean isUnarySupported() {
        return true;
    }
}
