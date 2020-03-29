package org.anchoranalysis.gui.graph.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.graph.execution.KernelExecutionTime;

/*-
 * #%L
 * anchor-gui-graph
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

import org.anchoranalysis.core.arithmetic.DivideUtilities;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarStateRatioPerKernel extends GraphDefinitionBarKernelExecutionTime {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3415790318095392951L;

	public GraphDefinitionBarStateRatioPerKernel( final String title ) throws InitException {
		
		super(
			title,
			new String[]{
				"rejection",
				"non-proposal",
				"acceptance"
			},
			(KernelExecutionTime item, int seriesNum) -> {
					
				switch(seriesNum) {
				case 0:
					return DivideUtilities.divideByZeroReplace( item.getRejectedCnt(), item.getExecutionCnt(), 0 );
				case 1:
					return DivideUtilities.divideByZeroReplace( item.getNotProposedCnt(), item.getExecutionCnt(), 0 );
				case 2:
					return DivideUtilities.divideByZeroReplace( item.getAcceptedCnt(), item.getExecutionCnt(), 0 );
				default:
					assert false;
					return 0.0;
				}
			},
			"Ratio of Kernel Proposals",
			true
		);
	}

	@Override
	public String getShortTitle() {
		return getTitle();
	}

	@Override
	public int totalIndex() {
		return -1;
	}
}
