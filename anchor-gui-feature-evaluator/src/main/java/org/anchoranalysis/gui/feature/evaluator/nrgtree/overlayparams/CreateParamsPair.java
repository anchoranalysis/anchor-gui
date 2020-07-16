/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree.overlayparams;

import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreatePairFromMark;

class CreateParamsPair {

    private Mark markSrc;
    private Mark markDest;
    private NRGStackWithParams raster;
    private Map<RegionMap, CreateFeatureInput<FeatureInput>> map = new HashMap<>();

    public CreateParamsPair(Mark markSrc, Mark markDest, NRGStackWithParams raster) {
        super();
        this.markSrc = markSrc;
        this.markDest = markDest;
        this.raster = raster;
    }

    public CreateFeatureInput<FeatureInput> getOrCreate(RegionMap regionMap) {

        CreateFeatureInput<FeatureInput> params = map.get(regionMap);

        if (params != null) {
            return params;
        }

        VoxelizedMarkMemo pmmSrc =
                PxlMarkMemoFactory.create(markSrc, raster.getNrgStack(), regionMap);
        VoxelizedMarkMemo pmmDest =
                PxlMarkMemoFactory.create(markDest, raster.getNrgStack(), regionMap);
        params = new CreatePairFromMark(pmmSrc, pmmDest, raster);
        return params;
    }
}
