/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class MemoUnaryFactory extends UnaryFactory {

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams raster)
            throws CreateException {
        return new FeatureInputSingleMemo(pmm, Optional.of(raster));
    }
}
