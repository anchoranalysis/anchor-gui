/*-
 * #%L
 * anchor-gui-feature-evaluator
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.feature.evaluator.energytree.overlayparams;

import java.util.HashMap;
import java.util.Map;
import org.anchoranalysis.feature.energy.EnergyStack;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.session.CreateFeatureInput;
import org.anchoranalysis.gui.feature.evaluator.energytree.createparams.CreateIndFromMark;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.voxelized.memo.PxlMarkMemoFactory;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;

class CreateParamsInd {

    private Mark mark;
    private EnergyStack raster;
    private Map<RegionMap, CreateFeatureInput<FeatureInput>> map = new HashMap<>();

    public CreateParamsInd(Mark mark, EnergyStack raster) {
        super();
        this.mark = mark;
        this.raster = raster;
    }

    public CreateFeatureInput<FeatureInput> getOrCreate(RegionMap regionMap) {

        CreateFeatureInput<FeatureInput> params = map.get(regionMap);

        if (params != null) {
            return params;
        }

        VoxelizedMarkMemo pmm = PxlMarkMemoFactory.create(mark, raster.withoutParams(), regionMap);
        assert (pmm != null);
        return new CreateIndFromMark(pmm, raster);
    }
}
