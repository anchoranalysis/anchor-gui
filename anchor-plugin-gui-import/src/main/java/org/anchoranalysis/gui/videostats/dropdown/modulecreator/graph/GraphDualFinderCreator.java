/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import java.util.ArrayList;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.creator.GraphFromDualFinderCreator;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;

public class GraphDualFinderCreator<T> extends ContextualModuleCreator {

    private final GraphFromDualFinderCreator<T> creator;
    private final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory;
    private final FinderCSVStats finderCSVStats;
    private final GraphColorScheme graphColorScheme;

    public GraphDualFinderCreator(
            GraphFromDualFinderCreator<T> creator,
            FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
            FinderCSVStats finderCSVStats,
            GraphColorScheme graphColorScheme) {
        super();
        this.creator = creator;
        this.finderCfgNRGHistory = finderCfgNRGHistory;
        this.finderCSVStats = finderCSVStats;
        this.graphColorScheme = graphColorScheme;
    }

    @Override
    public NamedModule[] create(
            String namePrefix,
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            VideoStatsModuleGlobalParams mpg)
            throws CreateException {

        ArrayList<NamedModule> outList = new ArrayList<>();

        if (!finderCfgNRGHistory.exists() && !finderCSVStats.exists()) {
            return new NamedModule[] {};
        }

        VideoStatsModuleCreator preferredCreator = null;

        GraphDefinition<T> definition = creator.createGraphDefinition(graphColorScheme);

        // Priority given to CSV stats if available
        if (finderCSVStats.exists()) {
            preferredCreator =
                    creator.createGraphModule(
                            namePrefix, definition, finderCfgNRGHistory, finderCSVStats, true);
            VideoStatsModuleCreatorAndAdder creatorAndAdder =
                    new VideoStatsModuleCreatorAndAdder(adder, preferredCreator);
            outList.add(
                    new NamedModule(
                            definition.getTitle(), creatorAndAdder, definition.getShortTitle()));
        }

        // Let's make another
        if (finderCfgNRGHistory.exists()) {
            VideoStatsModuleCreator moduleCreator =
                    creator.createGraphModule(
                            namePrefix, definition, finderCfgNRGHistory, finderCSVStats, false);
            VideoStatsModuleCreatorAndAdder creatorAndAdder =
                    new VideoStatsModuleCreatorAndAdder(adder, moduleCreator);
            outList.add(
                    new NamedModule(
                            String.format("%s (Instance CfgNRG Data)", definition.getTitle()),
                            creatorAndAdder,
                            definition.getShortTitle()));
        }

        return outList.toArray(new NamedModule[] {});
    }
}
