package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.arithmetic.RunningSumCollection;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.gui.graph.creator.IterAndExecutionTime;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.mpp.sgmn.kernel.proposer.KernelIterDescription;

import ch.ethz.biol.cell.mpp.gui.graph.jfreechart.bar.KernelExecutionTime;
import ch.ethz.biol.cell.mpp.gui.graph.jfreechart.bar.KernelExecutionTimeAllEach;
import ch.ethz.biol.cell.mpp.nrg.CfgNRGPixelized;

public class ExecutionTimesPerKernelUtilities {

	// A group size of -1 means we do the entire container
	public static List<IterAndExecutionTime> createExecutionTimesPerKernel( IBoundedIndexContainer<KernelIterDescription> cntr, KernelProposer<CfgNRGPixelized> kernelProposer, int groupSize ) throws GetOperationFailedException {
		
		ArrayList<IterAndExecutionTime> listOut = new ArrayList<>(); 
		
		int nextAgg = groupSize != -1 ? cntr.getMinimumIndex() + groupSize : -1;
				
		int numKernels = kernelProposer.getAllKernelFactories().size();
		
		RunningSumCollection sumTotal = new RunningSumCollection(numKernels);
		RunningSumCollection sumAccepted = new RunningSumCollection(numKernels);
		RunningSumCollection sumRejected = new RunningSumCollection(numKernels);
		RunningSumCollection sumProposed = new RunningSumCollection(numKernels);
		RunningSumCollection sumNotProposed = new RunningSumCollection(numKernels);
		
		// We assume all points exist
		for( int i=cntr.getMinimumIndex(); i<= cntr.getMaximumIndex(); i++) {

			KernelIterDescription kid = cntr.get(i);
			
			if (nextAgg != -1 && kid.getIter() > nextAgg) {
				
				KernelExecutionTimeAllEach executionTimes = createKernelExecutionTimeList(
					kernelProposer,
					sumTotal,
					sumAccepted,
					sumRejected,
					sumProposed,
					sumNotProposed
				);
				listOut.add( new IterAndExecutionTime(kid.getIter(), executionTimes ) );
				
				sumTotal.reset();
				sumAccepted.reset();
				sumRejected.reset();
				sumProposed.reset();
				sumNotProposed.reset();

				nextAgg += groupSize;
			}

			// We add to the appropriate bucket
			sumTotal.get( kid.getId() ).add( kid.getExecutionTime() );
			
			if (kid.isProposed()) {
				
				sumProposed.get( kid.getId() ).add( kid.getExecutionTime() );
				
				if (kid.isAccepted()) {
					sumAccepted.get( kid.getId() ).add( kid.getExecutionTime() );
				} else {
					sumRejected.get( kid.getId() ).add( kid.getExecutionTime() );
				}
					
			} else {
				sumNotProposed.get( kid.getId() ).add( kid.getExecutionTime() );
			}
		}

		KernelExecutionTimeAllEach executionTimes = createKernelExecutionTimeList(
			kernelProposer,
			sumTotal,
			sumAccepted,
			sumRejected,
			sumProposed,
			sumNotProposed
		);
		listOut.add( new IterAndExecutionTime( cntr.getMaximumIndex(), executionTimes) );

		return listOut;
	}
	
	private static KernelExecutionTimeAllEach createKernelExecutionTimeList(
		KernelProposer<CfgNRGPixelized> kernelProposer,
		RunningSumCollection sumTotal,
		RunningSumCollection sumAccepted,
		RunningSumCollection sumRejected,
		RunningSumCollection sumProposed,
		RunningSumCollection sumNotProposed		
	) {
		KernelExecutionTimeAllEach execTimes = new KernelExecutionTimeAllEach( kernelProposer.getNumKernel() );
		
		long totalExecutionTime = 0;
		long totalAcceptedTime = 0;
		long totalRejectedTime = 0;
		long totalProposedTime = 0;
		long totalNotProposedTime = 0;
		
		long totalExecutedCnt = 0;
		long totalAcceptedCnt = 0;
		long totalRejectedCnt = 0;
		long totalProposedCnt = 0;
		long totalNotProposedCnt = 0;
		
		// For each kernel
		for (int j=0; j<kernelProposer.getNumKernel(); j++) {
			KernelExecutionTime ket = new KernelExecutionTime( kernelProposer.getAllKernelFactories().get(j).getName() );
			
			ket.setExecutionTime(sumTotal.get(j).getSum() );
			ket.setAcceptedTime( sumAccepted.get(j).getSum() );
			ket.setRejectedTime( sumRejected.get(j).getSum() );
			ket.setProposedTime( sumProposed.get(j).getSum() );
			ket.setNotProposedTime( sumNotProposed.get(j).getSum() );
			
			totalExecutionTime += sumTotal.get(j).getSum();
			totalAcceptedTime += sumAccepted.get(j).getSum();
			totalRejectedTime += sumRejected.get(j).getSum();
			totalProposedTime += sumProposed.get(j).getSum();
			totalNotProposedTime += sumNotProposed.get(j).getSum();
			
			ket.setExecutionCnt( sumTotal.get(j).getCnt() );
			ket.setAcceptedCnt( sumAccepted.get(j).getCnt() );
			ket.setRejectedCnt( sumRejected.get(j).getCnt() );
			ket.setProposedCnt( sumProposed.get(j).getCnt() );
			ket.setNotProposedCnt( sumNotProposed.get(j).getCnt() );
			
			totalExecutedCnt += sumTotal.get(j).getCnt();
			totalAcceptedCnt += sumAccepted.get(j).getCnt();
			totalRejectedCnt += sumRejected.get(j).getCnt();
			totalProposedCnt += sumProposed.get(j).getCnt();
			totalNotProposedCnt += sumNotProposed.get(j).getCnt();
			
			execTimes.setKernel(j, ket);
		}
		
		KernelExecutionTime ketTotal = new KernelExecutionTime("any");
		
		ketTotal.setExecutionTime( totalExecutionTime );
		ketTotal.setAcceptedTime( totalAcceptedTime );
		ketTotal.setRejectedTime( totalRejectedTime );
		ketTotal.setProposedTime( totalProposedTime );
		ketTotal.setNotProposedTime( totalNotProposedTime );
		
		ketTotal.setExecutionCnt( totalExecutedCnt );
		ketTotal.setAcceptedCnt( totalAcceptedCnt );
		ketTotal.setRejectedCnt( totalRejectedCnt );
		ketTotal.setProposedCnt( totalProposedCnt );
		ketTotal.setNotProposedCnt( totalNotProposedCnt );
		
		execTimes.setAll( ketTotal );
		
		return execTimes;
	}
}
