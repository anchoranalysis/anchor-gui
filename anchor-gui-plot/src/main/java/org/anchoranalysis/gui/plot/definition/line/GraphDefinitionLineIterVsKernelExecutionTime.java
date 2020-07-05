package org.anchoranalysis.gui.plot.definition.line;

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
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.plot.creator.IterAndExecutionTime;

public class GraphDefinitionLineIterVsKernelExecutionTime extends GraphDefinition<IterAndExecutionTime> {

	// START BEAN PROPERITES
	@BeanField
	private GraphColorScheme graphColorScheme = new GraphColorScheme();
	// END BEAN PROPERTIES
	
	private String subTitle;
	private String title;
	private String yAxisLabel;
	private boolean ignoreRangeOutside;
	private String[] seriesTitles;
	private YValGetter<IterAndExecutionTime> yValGetter;

	
	public GraphDefinitionLineIterVsKernelExecutionTime( String title, String subTitle, String yAxisLabel, final YValGetter<IterAndExecutionTime> yValGetter, boolean ignoreRangeOutside ) {
		this( title, subTitle, yAxisLabel, new String[]{ subTitle }, yValGetter, ignoreRangeOutside );
	}
	
	public GraphDefinitionLineIterVsKernelExecutionTime( String title, String subTitle, String yAxisLabel, String[] seriesTitles, final YValGetter<IterAndExecutionTime> yValGetter, boolean ignoreRangeOutside ) {

		// We need to do this first, so that getTitle() will work
		this.title = title;
		this.subTitle = subTitle;
		this.yAxisLabel = yAxisLabel;
		this.seriesTitles = seriesTitles;
		this.ignoreRangeOutside = ignoreRangeOutside;
		this.yValGetter = yValGetter;
	}

	@Override
	public GraphInstance create( Iterator<IterAndExecutionTime> itr, Optional<AxisLimits> domainLimits, Optional<AxisLimits> rangeLimits ) throws CreateException {
		LinePlot<IterAndExecutionTime> delegate = new LinePlot<>(
			getTitle(),
			seriesTitles,
			yValGetter
		);
		delegate.setIgnoreRangeAxisOutside( ignoreRangeOutside );
		delegate.getLabels().setXY("Iteration", yAxisLabel);
		delegate.setGraphColorScheme(graphColorScheme);
		return delegate.create(itr, domainLimits, rangeLimits);
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isItemAccepted(IterAndExecutionTime item) {
		return true;
	}
	
	@Override
	public String getShortTitle() {
		return subTitle;
	}

	public String[] getSeriesTitles() {
		return seriesTitles;
	}

	public YValGetter<IterAndExecutionTime> getyValGetter() {
		return yValGetter;
	}

	public String getyAxisLabel() {
		return yAxisLabel;
	}

	public boolean isIgnoreRangeOutside() {
		return ignoreRangeOutside;
	}

	public GraphColorScheme getGraphColorScheme() {
		return graphColorScheme;
	}

	public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
		this.graphColorScheme = graphColorScheme;
	}

}
