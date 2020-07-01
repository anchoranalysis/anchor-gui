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
