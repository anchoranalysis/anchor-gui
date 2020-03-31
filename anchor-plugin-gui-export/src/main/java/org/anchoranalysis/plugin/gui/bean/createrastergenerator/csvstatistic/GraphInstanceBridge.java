package org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic;

/*-
 * #%L
 * anchor-plugin-gui-export
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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
import org.anchoranalysis.core.bridge.BridgeElementException;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.graph.BoundedIndexContainerIterator;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

/**
 * 
 * @author feehano
 *
 * @param <T> graph-item type
 */
class GraphInstanceBridge<T> implements IObjectBridge<MappedFrom<CSVStatistic>,GraphInstance> {
	
	// START: PARAMETERS IN
	private GraphDefinition<T> graphDefinition;
	private IBoundedIndexContainer<CSVStatistic> cntr;
	private IObjectBridge<CSVStatistic, T> elementBridge;
	// END: PARAMETERS IN
	
	private AxisLimits rangeLimits;

	public GraphInstanceBridge(GraphDefinition<T> graphDefinition,
			IBoundedIndexContainer<CSVStatistic> cntr,
			IObjectBridge<CSVStatistic, T> elementBridge
		) {
		super();
		this.graphDefinition = graphDefinition;
		this.cntr = cntr;
		this.elementBridge = elementBridge;
	}
	
	@Override
	public GraphInstance bridgeElement(MappedFrom<CSVStatistic> sourceObject)
			throws BridgeElementException {
		
		assert(graphDefinition!=null);
		
		// To go from a particular csv statistic to a graph instance
		
		// lets get the current iter
		int currentIndex = sourceObject.getOriginalIter();
		
		// The bridge between CSVStats and the graph
		BoundedIndexContainerBridgeWithoutIndex<CSVStatistic, T> boundBridge
			= new BoundedIndexContainerBridgeWithoutIndex<>(cntr, elementBridge);
		
		try {
			AxisLimits domainLimits = createLimitsFromCntr(cntr);
			
			// We create a graph of all index points, so we can calculate range limits that are static
			//   only the first time we execute the function
			if (rangeLimits==null) {
				rangeLimits = guessRangeLimits(boundBridge, domainLimits);
			}
			
			return graphDefinition.create(
				new BoundedIndexContainerIterator<>(boundBridge, 1000, currentIndex),
				domainLimits,
				rangeLimits
			);
		} catch (CreateException e) {
			throw new BridgeElementException(e);
		}
	}
	
	private static AxisLimits createLimitsFromCntr(IBoundedIndexContainer<CSVStatistic> cntr) {
		AxisLimits limits = new AxisLimits();
		limits.setAxisMin( cntr.getMinimumIndex() );
		limits.setAxisMax( cntr.getMaximumIndex() );
		return limits;
	}
	
	private AxisLimits guessRangeLimits(
		BoundedIndexContainerBridgeWithoutIndex<CSVStatistic, T> boundBridge,
		AxisLimits domainLimits
	) throws CreateException {
		Iterator<T> itrAll = new BoundedIndexContainerIterator<>(boundBridge, 1000);
		GraphInstance instance = graphDefinition.create(itrAll, domainLimits, null);
		return instance.getRangeAxisLimits();
	}
}
