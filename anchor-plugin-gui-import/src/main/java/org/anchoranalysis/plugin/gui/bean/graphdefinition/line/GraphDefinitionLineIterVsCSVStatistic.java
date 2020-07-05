package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

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


import java.util.Iterator;
import java.util.Optional;

import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.plot.index.LinePlot;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public abstract class GraphDefinitionLineIterVsCSVStatistic extends GraphDefinition<CSVStatistic> {

	private LinePlot<CSVStatistic> delegate;
	
	private String title;
	private String shortTitle;
	
	public GraphDefinitionLineIterVsCSVStatistic( String title, String[] seriesName, String yAxisLabel, YValGetter<CSVStatistic> yValGetter, GraphColorScheme graphColorScheme ) {
		this( title, title, seriesName, yAxisLabel, yValGetter, graphColorScheme );
	}
	
	public GraphDefinitionLineIterVsCSVStatistic( String title, String shortTitle, String[] seriesName, String yAxisLabel, YValGetter<CSVStatistic> yValGetter, GraphColorScheme graphColorScheme ) {
		this.title = title;
		this.shortTitle = shortTitle;
		
		delegate = new LinePlot<>(
			getTitle(),
			seriesName,
			yValGetter
		);
		delegate.getLabels().setX("Iteration");
		delegate.getLabels().setY(yAxisLabel);
		delegate.setGraphColorScheme(graphColorScheme);
	}
	

	@Override
	public GraphInstance create( Iterator<CSVStatistic> items, Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits ) throws CreateException {
		return delegate.create(items, domainLimits, rangeLimits);
	}

	@Override
	public String getTitle() {
		return title;
	}
	
	@Override
	public String getShortTitle() {
		return shortTitle;
	}
}
