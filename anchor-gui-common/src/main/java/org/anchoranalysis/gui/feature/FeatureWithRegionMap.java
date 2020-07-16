/* (C)2020 */
package org.anchoranalysis.gui.feature;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public class FeatureWithRegionMap<T extends FeatureInput> {

    private Feature<T> feature;
    private RegionMap regionMap;

    public FeatureWithRegionMap(Feature<T> feature, RegionMap regionMap) {
        super();
        assert (feature != null);
        assert (regionMap != null);
        this.feature = feature;
        this.regionMap = regionMap;
    }

    public Feature<T> getFeature() {
        return feature;
    }

    public RegionMap getRegionMap() {
        return regionMap;
    }
}
