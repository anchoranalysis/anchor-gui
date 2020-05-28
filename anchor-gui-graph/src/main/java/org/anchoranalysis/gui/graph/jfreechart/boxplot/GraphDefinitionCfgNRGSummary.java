package org.anchoranalysis.gui.graph.jfreechart.boxplot;

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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.anchoranalysis.anchor.graph.AxisLimits;
import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.anchor.graph.bean.GraphDefinition;
import org.anchoranalysis.anchor.graph.index.BoxPlot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.feature.CalculatedFeatureValues;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.jfree.data.statistics.BoxAndWhiskerItem;

public class GraphDefinitionCfgNRGSummary extends GraphDefinition<Integer> {

	// Integer is the index of the feature
	
	private BoxPlot<Integer> delegate;
	
	@SuppressWarnings("rawtypes")
	private static BoxAndWhiskerItem createFromCalculatedFeatureValues( CalculatedFeatureValues calculatedFeatures, int featureIndex ) {
		double min = calculatedFeatures.getFeatureMin(featureIndex);
		double max = calculatedFeatures.getFeatureMax(featureIndex);
		double mean = calculatedFeatures.getFeatureMean(featureIndex);
		double median = calculatedFeatures.getFeatureMedian(featureIndex);
		double q1 = calculatedFeatures.getFeatureQuantile(featureIndex,0.25);
		double q3 = calculatedFeatures.getFeatureQuantile(featureIndex,0.75);
		return new BoxAndWhiskerItem( mean, median, q1, q3, min, max, min, max, new ArrayList() );
	}
	
	public GraphDefinitionCfgNRGSummary( final List<CalculatedFeatureValues> tableModelList, final FeatureListWithRegionMap<?> features, final List<String> seriesNames) throws InitException {
		
		delegate = new BoxPlot<>(
				getTitle(),
				seriesNames.toArray( new String[]{} ),
				(Integer item, int seriesNum) -> features.get(item).getFeature().getFriendlyName(),
				(Integer item, int seriesNum) -> {
					CalculatedFeatureValues calculatedFeatures = tableModelList.get(seriesNum);
					return createFromCalculatedFeatureValues( calculatedFeatures, item );
				},
				null
		);
		delegate.getLabels().setX("Object Sets");
		delegate.getLabels().setY("Features");
	}
	
	@Override
	public GraphInstance create(Iterator<Integer> items,
			Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits) throws CreateException {
		return delegate.create(items, domainLimits, rangeLimits);
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
