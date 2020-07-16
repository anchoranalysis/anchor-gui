/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.boxplot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.index.BoxPlot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.feature.CalculatedFeatureValues;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.jfree.data.statistics.BoxAndWhiskerItem;

public class GraphDefinitionCfgNRGSummary extends GraphDefinition<Integer> {

    // Integer is the index of the feature

    private BoxPlot<Integer> delegate;

    private static BoxAndWhiskerItem createFromCalculatedFeatureValues(
            CalculatedFeatureValues calculatedFeatures, int featureIndex) {
        double min = calculatedFeatures.getFeatureMin(featureIndex);
        double max = calculatedFeatures.getFeatureMax(featureIndex);
        double mean = calculatedFeatures.getFeatureMean(featureIndex);
        double median = calculatedFeatures.getFeatureMedian(featureIndex);
        double q1 = calculatedFeatures.getFeatureQuantile(featureIndex, 0.25);
        double q3 = calculatedFeatures.getFeatureQuantile(featureIndex, 0.75);
        return new BoxAndWhiskerItem(mean, median, q1, q3, min, max, min, max, new ArrayList<>());
    }

    public GraphDefinitionCfgNRGSummary(
            final List<CalculatedFeatureValues> tableModelList,
            final FeatureListWithRegionMap<?> features,
            final List<String> seriesNames) {
        delegate =
                new BoxPlot<>(
                        getTitle(),
                        seriesNames.toArray(new String[] {}),
                        (Integer item, int seriesNum) ->
                                features.get(item).getFeature().getFriendlyName(),
                        (Integer item, int seriesNum) -> {
                            CalculatedFeatureValues calculatedFeatures =
                                    tableModelList.get(seriesNum);
                            return createFromCalculatedFeatureValues(calculatedFeatures, item);
                        },
                        null);
        delegate.getLabels().setX("Object Sets");
        delegate.getLabels().setY("Features");
    }

    @Override
    public GraphInstance create(
            Iterator<Integer> items,
            Optional<AxisLimits> domainLimits,
            Optional<AxisLimits> rangeLimits)
            throws CreateException {
        return delegate.createWithRangeLimits(items, rangeLimits);
    }

    @Override
    public boolean isItemAccepted(Integer item) {
        return true;
    }

    @Override
    public String getTitle() {
        return "Features Calculated on Object Sets";
    }

    @Override
    public String getShortTitle() {
        return getTitle();
    }
}
