package org.anchoranalysis.gui.feature;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.feature.bean.Feature;

public class FeatureWithRegionMap {
	private Feature feature;
	private RegionMap regionMap;
	
	public FeatureWithRegionMap(Feature feature, RegionMap regionMap) {
		super();
		assert(feature!=null);
		assert(regionMap!=null);
		this.feature = feature;
		this.regionMap = regionMap;
	}

	public Feature getFeature() {
		return feature;
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}
}