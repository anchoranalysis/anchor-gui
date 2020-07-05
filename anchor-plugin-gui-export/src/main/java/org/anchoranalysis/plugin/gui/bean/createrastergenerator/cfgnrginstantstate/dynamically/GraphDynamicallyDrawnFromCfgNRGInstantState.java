package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;

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


import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGraph;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic.GraphDynamicallyDrawnFromCSVStatistic;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;


/**
 * 
 * @author feehano
 *
 * @param <T> graph-item type
 */
public abstract class GraphDynamicallyDrawnFromCfgNRGInstantState<T> extends CreateRasterGraph<T,CfgNRGInstantState> {

	// Delayed instantiation of delegate so parameters are already filled
	private GraphDynamicallyDrawnFromCSVStatistic<T> delegate;
	
	private FunctionWithException<CSVStatistic,T,CreateException> elementBridge;
	
	public GraphDynamicallyDrawnFromCfgNRGInstantState( FunctionWithException<CSVStatistic,T,CreateException> elementBridge) {
		super();
		this.elementBridge = elementBridge;
	}
	
	private GraphDynamicallyDrawnFromCSVStatistic<T> createDelegateIfNecessary() {
		if (delegate==null) {
			delegate = new GraphDynamicallyDrawnFromCSVStatistic<>(elementBridge);
			delegate.setHeight( getHeight() );
			delegate.setWidth( getWidth() );
			delegate.setGraphDefinition( getGraphDefinition() );
		}
		return delegate;
	}
	
	
	@Override
	public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(ExportTaskParams params) throws CreateException {

		IterableObjectGenerator<MappedFrom<CSVStatistic>, Stack> generator = createDelegateIfNecessary().createGenerator(params);
		
		try {
			return new IterableObjectGeneratorBridge<>(
				generator,
				new FindNearestStatisticBridge(
					params.getFinderCsvStatistics().get()
				)
			);
			
		} catch (GetOperationFailedException e) {
			throw new CreateException(e);
		}
	}

	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		return createDelegateIfNecessary().hasNecessaryParams(params) && params.getFinderCsvStatistics()!=null;
	}
	
}