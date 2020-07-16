/*-
 * #%L
 * anchor-gui-plot
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;


import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarProposalType extends GraphDefinitionBarKernelExecutionTime {
	
	public static enum ProposalType {
		REJECTED,
		NOT_PROPOSED,
		ACCEPTED
	}
	
	private static String getNameForProposalType( ProposalType proposalType ) {
		switch( proposalType ) {
		case REJECTED:
			return "rejected";
		case NOT_PROPOSED:
			return "not proposed";
		case ACCEPTED:
			return "accepted";
		default:
			assert false;
			return "invalid";
		}
	}
	
	public GraphDefinitionBarProposalType( final String title, final ProposalType proposalType ) throws InitException {
		
		super(
			title,
			new String[]{
				getNameForProposalType(proposalType)
			},
			(KernelExecutionTime item, int seriesNum) -> {
				
				switch(proposalType) {
				case REJECTED:
					return (double) item.getRejectedCnt();
				case NOT_PROPOSED:
					return (double) item.getNotProposedCnt();
				case ACCEPTED:
					return (double) item.getAcceptedCnt();
				default:
					assert false;
					return 0.0;
				}
			},
			"# Iterations",
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
