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
import org.anchoranalysis.gui.feature.evaluator.nrgtree.createparams.CreateIndFromMark;

class CreateParamsInd {

    private Mark mark;
    private NRGStackWithParams raster;
    private Map<RegionMap, CreateFeatureInput<FeatureInput>> map = new HashMap<>();

    public CreateParamsInd(Mark mark, NRGStackWithParams raster) {
        super();
        this.mark = mark;
        this.raster = raster;
    }

    public CreateFeatureInput<FeatureInput> getOrCreate(RegionMap regionMap) {

        CreateFeatureInput<FeatureInput> params = map.get(regionMap);

        if (params != null) {
            return params;
        }

        VoxelizedMarkMemo pmm = PxlMarkMemoFactory.create(mark, raster.getNrgStack(), regionMap);
        assert (pmm != null);
        return new CreateIndFromMark(pmm, raster);
    }
}
