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
package org.anchoranalysis.gui.plot.definition.line;


import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.anchor.plot.GetForSeries;
import org.anchoranalysis.anchor.plot.index.LinePlot.YValGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.plot.creator.IterAndExecutionTime;

public class GraphDefinitionLineIterVsKernelExecutionTimeByState extends GraphDefinitionLineIterVsKernelExecutionTime {

	private final GetForSeries<KernelExecutionTime,Double> ketGetter;
	
	// -1 as a kernelID indicates a summation of them all
	public GraphDefinitionLineIterVsKernelExecutionTimeByState( String title, String subTitle, String yAxisLabel, final int kernelID, String[] seriesTitles, final GetForSeries<KernelExecutionTime,Double> ketGetter, boolean ignoreRangeOutside ) {

		super(
			title,
			subTitle,
			yAxisLabel,
			seriesTitles,
			new YValGetter<IterAndExecutionTime>() {

				@Override
				public double getYVal(IterAndExecutionTime item, int yIndex) throws GetOperationFailedException {
					
					KernelExecutionTime ket = item.getExecutionTimes().getForKernelID(kernelID);
					return ketGetter.get(ket, yIndex);
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
