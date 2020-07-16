/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate.dynamically;

import org.anchoranalysis.gui.plot.creator.BridgedGraphCfgSizeCreator;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsCfgSize;

public class GraphCfgSizeDynamicallyDrawnFromCfgNRGInstantState
        extends GraphDynamicallyDrawnFromCfgNRGInstantState<GraphDefinitionLineIterVsCfgSize.Item> {

    public GraphCfgSizeDynamicallyDrawnFromCfgNRGInstantState() {
        super(new BridgedGraphCfgSizeCreator().createCSVStatisticBridge());
    }
}
