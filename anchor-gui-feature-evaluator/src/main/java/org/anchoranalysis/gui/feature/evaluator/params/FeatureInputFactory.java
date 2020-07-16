/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public interface FeatureInputFactory {

    FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams raster) throws CreateException;

    FeatureInput create(VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, NRGStackWithParams raster)
            throws CreateException;

    FeatureInput create(MemoCollection pmmhList, NRGStackWithParams raster) throws CreateException;

    boolean isPairwiseSupported();

    boolean isUnarySupported();
}
