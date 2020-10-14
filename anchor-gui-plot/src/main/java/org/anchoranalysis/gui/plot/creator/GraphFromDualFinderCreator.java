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

package org.anchoranalysis.gui.plot.creator;

import java.util.Iterator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.finder.FinderCSVStats;
import org.anchoranalysis.gui.finder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.BoundedIndexContainerIterator;
import org.anchoranalysis.gui.plot.panel.ClickableGraphFactory;
import org.anchoranalysis.gui.plot.visualvm.InternalFrameGraphAsModule;
import org.anchoranalysis.gui.reassign.FrameTitleCreator;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.plot.bean.Plot;
import org.anchoranalysis.plot.bean.colorscheme.PlotColorScheme;

public interface GraphFromDualFinderCreator<T> {

    BoundedIndexContainer<T> createContainer(final FinderCSVStats finderCSVStats)
            throws CreateException;

    BoundedIndexContainer<T> createContainer(
            final FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory)
            throws CreateException;

    Plot<T> createGraphDefinition(PlotColorScheme graphColorScheme) throws CreateException;

    // useCSV is a flag indicating which of the two to use
    public default VideoStatsModuleCreator createGraphModule(
            final String windowTitlePrefix,
            final Plot<T> definition,
            final FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory,
            final FinderCSVStats finderCSVStats,
            final boolean useCSV) {
        return new VideoStatsModuleCreator() {

            @Override
            public void createAndAddVideoStatsModule(AddVideoStatsModule adder)
                    throws VideoStatsModuleCreateException {

                try {
                    // We calculate our container
                    BoundedIndexContainer<T> cntr;
                    if (useCSV && finderCSVStats.exists()) {
                        cntr = createContainer(finderCSVStats);
                    } else if (finderMarksHistory.exists()) {
                        cntr = createContainer(finderMarksHistory);
                    } else {
                        return;
                    }

                    Iterator<T> itr = new BoundedIndexContainerIterator<>(cntr, 1000);

                    String graphFrameTitle =
                            FrameTitleCreator.prefix(windowTitlePrefix, definition.getTitle());

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
