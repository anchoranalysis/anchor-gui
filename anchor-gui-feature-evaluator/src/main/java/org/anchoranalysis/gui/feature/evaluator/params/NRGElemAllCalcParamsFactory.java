/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.mark.MemoCollection;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

public class NRGElemAllCalcParamsFactory extends AllFactory {

    @Override
    public FeatureInput create(MemoCollection pmmhList, NRGStackWithParams raster)
            throws CreateException {
        return new FeatureInputAllMemo(pmmhList, raster);
    }
}
