/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public abstract class AllFactory implements FeatureInputFactory {

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams raster)
            throws CreateException {
        throw new CreateException("unsupported");
    }

    @Override
    public FeatureInput create(
            VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, NRGStackWithParams raster)
            throws CreateException {
        throw new CreateException("unsupported");
    }

    @Override
    public boolean isPairwiseSupported() {
        return false;
    }

    @Override
    public boolean isUnarySupported() {
        return false;
    }
}
