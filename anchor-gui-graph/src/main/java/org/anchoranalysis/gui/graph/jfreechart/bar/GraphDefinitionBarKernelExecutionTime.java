package org.anchoranalysis.gui.graph.jfreechart.bar;

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

import org.anchoranalysis.anchor.graph.AxisLimits;
import org.anchoranalysis.anchor.graph.GetForSeries;
import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.anchor.graph.bean.GraphDefinition;
import org.anchoranalysis.anchor.graph.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.graph.index.BarChart;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.graph.definition.line.GraphDefinitionLineIterVsKernelExecutionTimeByState;

import ch.ethz.biol.cell.mpp.gui.graph.jfreechart.bar.KernelExecutionTime;

public abstract class GraphDefinitionBarKernelExecutionTime extends GraphDefinition<KernelExecutionTime> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7871592334601253469L;
	private BarChart<KernelExecutionTime> delegate;
	private String title;
	private String[] seriesNames;
	private String yAxisLabel;
	
	public GraphDefinitionBarKernelExecutionTime( final String title, final String[] seriesNames, GetForSeries<KernelExecutionTime,Double> valueGetter, String yAxisLabel, boolean stacked ) throws InitException {
		
		this.title = title;
		this.seriesNames = seriesNames;
		this.yAxisLabel = yAxisLabel;
		
		delegate = new BarChart<>(
				getTitle(),
				seriesNames,
				(KernelExecutionTime item, int seriesNum) -> item.getKernelName(), 
				valueGetter,
				null,
				stacked
		);
		delegate.getLabels().setX("Kernel");
		delegate.getLabels().setY( yAxisLabel );
	}
	
	
	// Total indicating which of the series, represents a total, or sum, or "any" column.  -1 if none exists
	public abstract int totalIndex();
	
	public GraphDefinitionLineIterVsKernelExecutionTimeByState createLineGraphDefinition( boolean ignoreRangeOutside ) {
		
		GraphDefinitionLineIterVsKernelExecutionTimeByState definition = new GraphDefinitionLineIterVsKernelExecutionTimeByState(
				"Iteration vs " + title,
				title,
				yAxisLabel,
				-1,
				seriesNames,
				delegate.getyValGetter(),
				ignoreRangeOutside
		);
		definition.setGraphColorScheme( delegate.getGraphColorScheme() );
		return definition;
	}
	
	@Override
	public GraphInstance create( Iterator<KernelExecutionTime> items, AxisLimits domainLimits, AxisLimits rangeLimits ) throws CreateException {
		return delegate.create( items, domainLimits, rangeLimits );
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isItemAccepted(KernelExecutionTime item) {
		return true;
	}
	
	// START BEAN PROPERTIES
	public boolean isShowDomainAxis() {
		return delegate.isShowDomainAxis();
	}

	public void setShowDomainAxis(boolean showDomainAxis) {
		delegate.setShowDomainAxis(showDomainAxis);
	}
	// END BEAN PROPERTIES


	public GraphColorScheme getGraphColorScheme() {
		return delegate.getGraphColorScheme();
	}


	public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
		delegate.setGraphColorScheme(graphColorScheme);
	}

}
