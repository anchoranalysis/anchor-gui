package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import org.anchoranalysis.anchor.graph.GraphInstance;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.graph.NRGGraphItem;
import org.anchoranalysis.core.error.CreateException;

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
import org.anchoranalysis.gui.graph.creator.GenerateGraphNRGBreakdownFromInstantState;
import org.anchoranalysis.gui.graph.panel.ClickableGraphInstance;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGraph;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class GraphFromCfgNRGInstantState extends CreateRasterGraph<NRGGraphItem,CfgNRGInstantState> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7808671504168446373L;

	@Override
	public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator( ExportTaskParams params ) throws CreateException {
		
		IterableObjectGenerator<GraphInstance,Stack> generator = createGraphInstanceGenerator();
		
		return new IterableObjectGeneratorBridge<>(
			createBridge(generator, params),
			elem -> elem.getObj()
		);
	}
	
	private IterableObjectGeneratorBridge<Stack, CfgNRGInstantState, ClickableGraphInstance> createBridge(
		IterableObjectGenerator<GraphInstance,Stack> generator,
		ExportTaskParams params
	) {
		// Presents a generator for a GraphInstance as a generator for ClickableGraphInstance
		IterableObjectGeneratorBridge<Stack,ClickableGraphInstance,GraphInstance> clickableGenerator = new IterableObjectGeneratorBridge<>(
			generator,
			a->a.getGraphInstance()
		);
		
		// Presents a generator for a ClickableGraphInstance as a generator for Stack
		return new IterableObjectGeneratorBridge<Stack, CfgNRGInstantState, ClickableGraphInstance>(
			clickableGenerator,
			new GenerateGraphNRGBreakdownFromInstantState(
				getGraphDefinition(),
				params.getColorIndexMarks()
			)
		);	
	}

	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		return params.getColorIndexMarks() != null;
	}
}