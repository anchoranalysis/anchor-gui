package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;

/*-
 * #%L
 * anchor-gui-plot
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

import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarExecutionTimePerKernel extends GraphDefinitionBarKernelExecutionTime {
	
	public GraphDefinitionBarExecutionTimePerKernel( final String title ) throws InitException {
		
		super(
			title,
			new String[]{
				"rejected",
				"not proposed",
				"accepted"
			},
			(KernelExecutionTime item, int seriesNum) -> {
					
				switch(seriesNum) {
				case 0:
					return item.getRejectedTime();
				case 1:
					return item.getNotProposedTime();
				case 2:
					return item.getAcceptedTime();
				case 3:
					// Just for total index, if re-used - we do not show
					return item.getExecutionTime();
				default:
					assert false;
					return 0.0;
				}
			},
			"Execution Time (ms)",
			true
		);
	}

	@Override
	public String getShortTitle() {
		return getTitle();
	}

	@Override
	public int totalIndex() {
		return 3;
	}
}
