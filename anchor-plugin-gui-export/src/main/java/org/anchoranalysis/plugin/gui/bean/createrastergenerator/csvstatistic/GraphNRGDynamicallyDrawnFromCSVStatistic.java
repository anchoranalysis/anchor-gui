/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.csvstatistic;

import org.anchoranalysis.gui.plot.creator.BridgedGraphNRGCreator;
import org.anchoranalysis.gui.plot.definition.line.GraphDefinitionLineIterVsNRG;

public class GraphNRGDynamicallyDrawnFromCSVStatistic
        extends GraphDynamicallyDrawnFromCSVStatistic<GraphDefinitionLineIterVsNRG.Item> {

    public GraphNRGDynamicallyDrawnFromCSVStatistic() {
        super(new BridgedGraphNRGCreator().createCSVStatisticBridge());
    }
}
