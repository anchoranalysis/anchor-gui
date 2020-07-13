package org.anchoranalysis.plugin.gui.bean.exporttask;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;

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


import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class ExportTaskCSVStatistic extends ExportTaskRasterGeneratorFromBoundedIndexContainer<CSVStatistic> {
		
	private static class ExportTaskParamsCSVStatisticContainerBridge implements FunctionWithException<
		ExportTaskParams,
		BoundedIndexContainer<CSVStatistic>,
		OperationFailedException
	> {

		@Override
		public BoundedIndexContainer<CSVStatistic> apply(
				ExportTaskParams sourceObject) throws OperationFailedException {
			try {
				return sourceObject.getFinderCsvStatistics().get();
			} catch (GetOperationFailedException e) {
				throw new OperationFailedException(e);
			}
		}
	}
	
	public ExportTaskCSVStatistic() {
		super();
	}
	
	@Override
	public void init() {
		setBridge( new ExportTaskParamsCSVStatisticContainerBridge() );
	}
}
