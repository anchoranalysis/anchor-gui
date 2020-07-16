/* (C)2020 */
package org.anchoranalysis.gui.plot.creator;

import java.util.Iterator;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.BoundedIndexContainerIterator;
import org.anchoranalysis.gui.plot.panel.ClickableGraphFactory;
import org.anchoranalysis.gui.plot.visualvm.InternalFrameGraphAsModule;
import org.anchoranalysis.gui.reassign.FrameTitleGenerator;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;

public interface GraphFromDualFinderCreator<T> {

    BoundedIndexContainer<T> createCntr(final FinderCSVStats finderCSVStats) throws CreateException;

    BoundedIndexContainer<T> createCntr(
            final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory)
            throws CreateException;

    GraphDefinition<T> createGraphDefinition(GraphColorScheme graphColorScheme)
            throws CreateException;

    // useCSV is a flag indicating which of the two to use
    public default VideoStatsModuleCreator createGraphModule(
            final String windowTitlePrefix,
            final GraphDefinition<T> definition,
            final FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory,
            final FinderCSVStats finderCSVStats,
            final boolean useCSV) {
        return new VideoStatsModuleCreator() {

            @Override
            public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
                    throws VideoStatsModuleCreateException {

                try {
                    // We calculate our container
                    BoundedIndexContainer<T> cntr;
                    if (useCSV && finderCSVStats.exists()) {
                        cntr = createCntr(finderCSVStats);
                    } else if (finderCfgNRGHistory.exists()) {
                        cntr = createCntr(finderCfgNRGHistory);
                    } else {
                        return;
                    }

                    Iterator<T> itr = new BoundedIndexContainerIterator<>(cntr, 1000);

                    String graphFrameTitle =
                            new FrameTitleGenerator()
                                    .genFramePrefix(windowTitlePrefix, definition.getTitle());

                    InternalFrameGraphAsModule frame =
                            new InternalFrameGraphAsModule(
                                    graphFrameTitle,
                                    ClickableGraphFactory.create(definition, itr, null, null));

                    ModuleAddUtilities.add(adder, frame.moduleCreator());

                } catch (CreateException e) {
                    throw new VideoStatsModuleCreateException(e);
                }
            }
        };
    }
}
