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

import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGraph;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;


/**
 * 
 * @author feehano
 *
 * @param <T> graph-item type
 */
public class GraphDynamicallyDrawnFromCSVStatistic<T> extends CreateRasterGraph<T,CSVStatistic> {

	private IObjectBridge<CSVStatistic,T,CreateException> elementBridge;
	
	public GraphDynamicallyDrawnFromCSVStatistic(IObjectBridge<CSVStatistic, T,CreateException> elementBridge) {
		super();
		this.elementBridge = elementBridge;
	}
	
	@Override
	public IterableObjectGenerator<MappedFrom<CSVStatistic>, Stack> createGenerator(
			final ExportTaskParams params) throws CreateException {
		
		assert( getGraphDefinition()!=null );
		
		try {
			IObjectBridge<MappedFrom<CSVStatistic>,GraphInstance,CreateException> bridge = new GraphInstanceBridge<T>(
				getGraphDefinition(),
				params.getFinderCsvStatistics().get(),
				elementBridge
			);
			
			return new IterableObjectGeneratorBridge<>(
				createGraphInstanceGenerator(),
				bridge
			);
			
		} catch (GetOperationFailedException e) {
			throw new CreateException(e);
		}
	}

	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		return params.getFinderCsvStatistics() != null;
	}
}
