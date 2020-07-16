/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class UnsupportedFactory implements FeatureInputFactory {

    private static final String UNSUPPORTED_MESSAGE = "unsupported";

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams nrgStack)
            throws CreateException {
        throw new CreateException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public FeatureInput create(
            VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, NRGStackWithParams raster)
            throws CreateException {
        throw new CreateException(UNSUPPORTED_MESSAGE);
    }

    @Override
    public FeatureInput create(MemoCollection pmmhList, NRGStackWithParams raster)
            throws CreateException {
        throw new CreateException(UNSUPPORTED_MESSAGE);
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
