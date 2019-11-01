package org.anchoranalysis.gui.feature;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;

// Associates a RegionMap with each feature
public class FeatureListWithRegionMap implements Iterable<FeatureWithRegionMap>{

	private List<FeatureWithRegionMap> list = new ArrayList<>();
	
	public FeatureListWithRegionMap() {
		super();
	}
	
	public FeatureWithRegionMap get( int index ) {
		return list.get(index);
	}
	
	public FeatureList createFeatureList() {
		FeatureList fl = new FeatureList();
		for (FeatureWithRegionMap f : list) {
			fl.add(f.getFeature());
		}
		return fl;
	}
	
	
	public void sortAlphaAscend() {
		Comparator<FeatureWithRegionMap> c = new Comparator<FeatureWithRegionMap>() {

			@Override
			public int compare(FeatureWithRegionMap o1, FeatureWithRegionMap o2) {
				String s1 = o1.getFeature().getFriendlyName();
				String s2 = o2.getFeature().getFriendlyName();
				return s1.compareTo(s2);
			}
			
		};
		Collections.sort(list, c);
	}

	public void add( Feature feature, RegionMap regionMap ) {
		list.add( new FeatureWithRegionMap(feature, regionMap) );
	}
	
	public int size() {
		return list.size();
	}
	

	@Override
	public Iterator<FeatureWithRegionMap> iterator() {
		return list.iterator();
	}
}
