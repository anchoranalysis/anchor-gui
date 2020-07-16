/* (C)2020 */
package org.anchoranalysis.gui.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;

// Associates a RegionMap with each feature
public class FeatureListWithRegionMap<T extends FeatureInput>
        implements Iterable<FeatureWithRegionMap<T>> {

    private List<FeatureWithRegionMap<T>> list = new ArrayList<>();

    public FeatureListWithRegionMap() {
        super();
    }

    public FeatureWithRegionMap<T> get(int index) {
        return list.get(index);
    }

    public FeatureList<T> createFeatureList() {
        return FeatureListFactory.mapFrom(list, FeatureWithRegionMap::getFeature);
    }

    @SuppressWarnings("unchecked")
    public FeatureListWithRegionMap<FeatureInput> upcast() {
        return (FeatureListWithRegionMap<FeatureInput>) this;
    }

    public void sortAlphaAscend() {
        Comparator<FeatureWithRegionMap<T>> c =
                new Comparator<FeatureWithRegionMap<T>>() {

                    @Override
                    public int compare(FeatureWithRegionMap<T> o1, FeatureWithRegionMap<T> o2) {
                        String s1 = o1.getFeature().getFriendlyName();
                        String s2 = o2.getFeature().getFriendlyName();
                        return s1.compareTo(s2);
                    }
                };
        Collections.sort(list, c);
    }

    public void add(Feature<T> feature, RegionMap regionMap) {
        list.add(new FeatureWithRegionMap<>(feature, regionMap));
    }

    public int size() {
        return list.size();
    }

    @Override
    public Iterator<FeatureWithRegionMap<T>> iterator() {
        return list.iterator();
    }
}
