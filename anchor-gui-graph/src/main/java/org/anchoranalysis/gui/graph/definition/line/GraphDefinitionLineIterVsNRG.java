package org.anchoranalysis.gui.graph.definition.line;

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
import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.anchor.graph.bean.GraphDefinition;
import org.anchoranalysis.anchor.graph.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.anchor.graph.index.LinePlot;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.IIndexGetter;

public class GraphDefinitionLineIterVsNRG extends GraphDefinition<GraphDefinitionLineIterVsNRG.Item> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674715407251540690L;
	
	// START BEAN PROPERITES
	@BeanField
	private GraphColorScheme graphColorScheme = new GraphColorScheme();
	
	@BeanField
	private int minMaxIgnoreBeforeIndex = 0;
	// END BEAN PROPERTIES
	
	// Item
	public static class Item implements IIndexGetter {
		private int iter;
		private double nrg;
		
		public Item() {
			
		}
		
		public Item(int iter, double nrg) {
			super();
			this.iter = iter;
			this.nrg = nrg;
		}

		public int getIter() {
			return iter;
		}
		public void setIter(int iter) {
			this.iter = iter;
		}
		public double getNrg() {
			return nrg;
		}
		public void setNrg(double nrg) {
			this.nrg = nrg;
		}

		@Override
		public int getIndex() {
			return iter;
		}
		
	}
	
	public GraphDefinitionLineIterVsNRG() {
	}

	@Override
	public GraphInstance create( Iterator<GraphDefinitionLineIterVsNRG.Item> items, AxisLimits domainLimits, AxisLimits rangeLimits ) throws CreateException {
		LinePlot<GraphDefinitionLineIterVsNRG.Item> delegate = new LinePlot<>(
			getTitle(),
			new String[]{"NRG"},
			(Item item, int yIndex) -> raiseNrgToLog(item.getNrg())
		);
		delegate.setMinMaxIgnoreBeforeIndex(minMaxIgnoreBeforeIndex);
		delegate.getLabels().setXY("Iteration", "NRG");
		delegate.setYAxisMargins(1);
		delegate.setGraphColorScheme(graphColorScheme);
		return delegate.create( items, domainLimits, rangeLimits );
	}
	
	private static double raiseNrgToLog( double in ) {
		double raised = -1 * Math.log(in);

		return replaceInfiniteWithMax(raised);
	}
	
	private static double replaceInfiniteWithMax( double in ) {
		// Account for infinite values
		if (Double.isInfinite(in)) {
			return Double.MAX_VALUE;
		} else {
			return in;
		}
	}

	@Override
	public String getTitle() {
		return "NRG Graph";
	}

	@Override
	public boolean isItemAccepted(Item item) {
		return true;
	}

	@Override
	public String getShortTitle() {
		return getTitle();
	}

	public GraphColorScheme getGraphColorScheme() {
		return graphColorScheme;
	}

	public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
		this.graphColorScheme = graphColorScheme;
	}

	public int getMinMaxIgnoreBeforeIndex() {
		return minMaxIgnoreBeforeIndex;
	}

	public void setMinMaxIgnoreBeforeIndex(int minMaxIgnoreBeforeIndex) {
		this.minMaxIgnoreBeforeIndex = minMaxIgnoreBeforeIndex;
	}

}
