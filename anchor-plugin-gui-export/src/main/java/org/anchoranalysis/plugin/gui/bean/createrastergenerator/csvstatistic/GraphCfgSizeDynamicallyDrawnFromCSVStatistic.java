package org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic;

import org.anchoranalysis.gui.plot.creator.BridgedGraphCfgSizeCreator;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsCfgSize;

public class GraphCfgSizeDynamicallyDrawnFromCSVStatistic extends GraphDynamicallyDrawnFromCSVStatistic<GraphDefinitionLineIterVsCfgSize.Item> {

	public GraphCfgSizeDynamicallyDrawnFromCSVStatistic() {
		super(new BridgedGraphCfgSizeCreator().createCSVStatisticBridge());
	}
}
