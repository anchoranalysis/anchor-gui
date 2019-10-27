package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

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

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;

/**
 * 1. Finds the nearest (previous or equal) CSVStatistic
 * 2. Copies it, and updates the iteration to match the current iteration
 * 
 * @author feehano
 *
 */
class FindNearestStatisticBridge implements IObjectBridge<MappedFrom<CfgNRGInstantState>, MappedFrom<CSVStatistic>> {
	
	private IBoundedIndexContainer<CSVStatistic> cntr;
		
	public FindNearestStatisticBridge(IBoundedIndexContainer<CSVStatistic> cntr) {
		super();
		this.cntr = cntr;
	}

	@Override
	public MappedFrom<CSVStatistic> bridgeElement(MappedFrom<CfgNRGInstantState> sourceObject)
			throws GetOperationFailedException {
		int indexAdj = cntr.previousEqualIndex(sourceObject.getOriginalIter());
		CSVStatistic stats = cntr.get(indexAdj);
		
		return new MappedFrom<CSVStatistic>(
			sourceObject.getOriginalIter(),
			maybeDuplicate(stats, sourceObject.getOriginalIter())
		);
	}
	
	private CSVStatistic maybeDuplicate( CSVStatistic stats, int iterToImpose ) {
		// Duplicate and update iteration to match statistics
		if (stats.getIter()==iterToImpose) {
			return stats;
		} else {
			return copyUpdateIter(stats, iterToImpose );
		}
		
	}
	
	private CSVStatistic copyUpdateIter( CSVStatistic stats, int iterToImpose ) {
		CSVStatistic dup = stats.duplicate();
		dup.setIter(iterToImpose);
		return dup;
	}
}
