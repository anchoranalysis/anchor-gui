/*-
 * #%L
 * anchor-plugin-gui-import
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
package org.anchoranalysis.plugin.gui.bean.graphdefinition.line;


import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.anchor.plot.GetForSeries;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.plot.creator.IterAndExecutionTime;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsKernelExecutionTime;
import org.anchoranalysis.mpp.sgmn.bean.kernel.proposer.KernelProposer;

public class GraphDefinitionLineIterVsKernelExecutionTimeByKernel extends GraphDefinitionLineIterVsKernelExecutionTime {

	private final GetForSeries<KernelExecutionTime,Double> ketGetter;
	
	
	private static int[] createKernelIDArr( KernelProposer<CfgNRGPixelized> kernelProposer ) {
		
		int numKernels = kernelProposer.getNumKernel();
		int[] kernelID = new int[numKernels+1];
		
		kernelID[0] = -1;
		
		for (int i=0; i<numKernels; i++) {
			kernelID[i+1] = i;
		}
		return kernelID;
	}
	
	private static String[] createKernelTitleArr( KernelProposer<CfgNRGPixelized> kernelProposer ) {
		
		int numKernels = kernelProposer.getNumKernel();
		String[] kernelTitleArr = new String[ numKernels+1 ];
		
		kernelTitleArr[0] = "any";
		
		for (int i=0; i<numKernels; i++) {
			kernelTitleArr[i+1] = kernelProposer.getAllKernelFactories().get(i).getName();
		}
		
		return kernelTitleArr;
	}
	
	// -1 as a kernelID indicates a summation of them all
	public GraphDefinitionLineIterVsKernelExecutionTimeByKernel( String title, String subTitle, String yAxisLabel, final KernelProposer<CfgNRGPixelized> kernelProposer, final GetForSeries<KernelExecutionTime,Double> ketGetter, final int ketGetterIndex, boolean ignoreRangeOutside ) {

		super(
			title,
			subTitle,
			yAxisLabel,
			createKernelTitleArr(kernelProposer),
			new YValGetter<IterAndExecutionTime>() {

				private int[] seriesKernelIDs;
				
				@Override
				public double getYVal(IterAndExecutionTime item, int yIndex) throws GetOperationFailedException {
					
					if (seriesKernelIDs==null) {
						seriesKernelIDs = createKernelIDArr( kernelProposer );
					}
					
					int index = seriesKernelIDs[yIndex];
					
					KernelExecutionTime ket;
					if (index>=0) {
						ket = item.getExecutionTimes().getKernel(index);
					} else {
						ket = item.getExecutionTimes().getAll();
					}
					
					// We now use the Getter<KernelExecutionTime,Double>()
					
					return ketGetter.get(ket, ketGetterIndex);
				}
			},
			ignoreRangeOutside
		);
		this.ketGetter = ketGetter;
	}
	
	public GetForSeries<KernelExecutionTime,Double> getKernelExecutionTimeGetter() {
		return ketGetter;
	}
}
