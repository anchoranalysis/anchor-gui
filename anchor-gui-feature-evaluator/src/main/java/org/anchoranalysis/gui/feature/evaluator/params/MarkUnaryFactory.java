/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.bean.mark.FeatureInputMark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class MarkUnaryFactory extends UnaryFactory {

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams raster)
            throws CreateException {
        return new FeatureInputMark(pmm.getMark(), raster.getDimensions(), raster.getParams());
    }
}
