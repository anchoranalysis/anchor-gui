package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

import org.anchoranalysis.gui.plot.creator.BridgedGraphNRGCreator;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsNRG;

public class GraphNRGDynamicallyDrawnFromCfgNRGInstantState extends GraphDynamicallyDrawnFromCfgNRGInstantState<GraphDefinitionLineIterVsNRG.Item> {

	public GraphNRGDynamicallyDrawnFromCfgNRGInstantState() {
		super(new BridgedGraphNRGCreator().createCSVStatisticBridge());
	}
}
