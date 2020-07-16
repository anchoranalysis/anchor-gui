/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.plot.NRGGraphItem;
import org.anchoranalysis.anchor.plot.GraphInstance;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.plot.creator.GenerateGraphNRGBreakdownFromInstantState;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGraph;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

public class GraphFromCfgNRGInstantState
        extends CreateRasterGraph<NRGGraphItem, CfgNRGInstantState> {

    @Override
    public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(
            ExportTaskParams params) throws CreateException {

        IterableObjectGenerator<GraphInstance, Stack> generator = createGraphInstanceGenerator();

        return new IterableObjectGeneratorBridge<>(
                createBridge(generator, params), elem -> elem.getObj());
    }

    private IterableObjectGeneratorBridge<Stack, CfgNRGInstantState, ClickableGraphInstance>
            createBridge(
                    IterableObjectGenerator<GraphInstance, Stack> generator,
                    ExportTaskParams params) {
        // Presents a generator for a GraphInstance as a generator for ClickableGraphInstance
        IterableObjectGeneratorBridge<Stack, ClickableGraphInstance, GraphInstance>
                clickableGenerator =
                        new IterableObjectGeneratorBridge<>(generator, a -> a.getGraphInstance());

        // Presents a generator for a ClickableGraphInstance as a generator for Stack
        return new IterableObjectGeneratorBridge<Stack, CfgNRGInstantState, ClickableGraphInstance>(
                clickableGenerator,
                new GenerateGraphNRGBreakdownFromInstantState(
                        getGraphDefinition(), params.getColorIndexMarks()));
    }

    @Override
    public boolean hasNecessaryParams(ExportTaskParams params) {
        return params.getColorIndexMarks() != null;
    }
}
