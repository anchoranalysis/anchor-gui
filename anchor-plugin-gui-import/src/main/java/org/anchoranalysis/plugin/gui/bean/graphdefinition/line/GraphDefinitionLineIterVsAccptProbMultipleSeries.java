package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;

import org.anchoranalysis.anchor.graph.bean.colorscheme.GraphColorScheme;
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


import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;

public class GraphDefinitionLineIterVsAccptProbMultipleSeries extends GraphDefinitionLineIterVsCSVStatistic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3577323836458002110L;

	public GraphDefinitionLineIterVsAccptProbMultipleSeries( GraphColorScheme graphColorScheme ) {
		
		super(
			"Rate of Kernel Acceptance (Multiple series)",
			new String[]{"First", "All", "Random"},
			"Acceptance Rate",
			(CSVStatistic item, int yIndex) ->  {
				switch( yIndex ) {
				case 0:
					return item.getAccptProb();
				case 1:
					return item.getAccptProbAll();
				case 2:
					return item.getAccptProbRand();
				default:
					throw new GetOperationFailedException("Invalid yIndex value");
				}
			},
			graphColorScheme
		);
	}

	@Override
	public boolean isItemAccepted(CSVStatistic item) {
		return item.hasAccptProb() && item.hasAccptProbAll() && item.hasAccptProbRand();
	}
}
