/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.plot.NRGGraphItem;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.cfgnrg.CfgNRGInstantStateGraphPanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelFrameHistoryCfgNRGInstantState;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.creator.GenerateGraphNRGBreakdownFromInstantState;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

public class GraphNRGBreakdownModuleCreator extends VideoStatsModuleCreatorContext {

    private final GraphDefinition<NRGGraphItem> definition;
    private final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
    private final ColorIndex colorIndex;

    public GraphNRGBreakdownModuleCreator(
            GraphDefinition<NRGGraphItem> definition,
            FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
            ColorIndex colorIndex) {
        super();
        this.definition = definition;
        this.finderCfgNRGHistory = finderCfgNRGHistory;
        this.colorIndex = colorIndex;
    }

    @Override
    public boolean precondition() {
        return finderCfgNRGHistory.exists();
    }

    @Override
    public Optional<IModuleCreatorDefaultState> moduleCreator(
            DefaultModuleStateManager defaultStateManager,
            String namePrefix,
            VideoStatsModuleGlobalParams mpg)
            throws VideoStatsModuleCreateException {

        ErrorReporter errorReporter = mpg.getLogger().errorReporter();
        GenerateGraphNRGBreakdownFromInstantState generator =
                new GenerateGraphNRGBreakdownFromInstantState(definition, colorIndex);

        String graphFrameTitle =
                new FrameTitleGenerator().genFramePrefix(namePrefix, definition.getTitle());

        try {
            CfgNRGInstantStateGraphPanel tablePanel = new CfgNRGInstantStateGraphPanel(generator);

            StatePanelFrameHistoryCfgNRGInstantState frame =
                    new StatePanelFrameHistoryCfgNRGInstantState(graphFrameTitle, true);
            frame.init(
                    defaultStateManager.getState().getLinkState().getFrameIndex(),
                    finderCfgNRGHistory.get(),
                    tablePanel,
                    errorReporter);

            return Optional.of(frame.moduleCreator());

        } catch (GetOperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public String title() {
        return definition.getTitle();
    }

    @Override
    public Optional<String> shortTitle() {
        return Optional.of(definition.getShortTitle());
    }
}
