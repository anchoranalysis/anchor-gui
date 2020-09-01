/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.plot.creator.GraphFromDualFinderCreator;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModuleSupplier;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.plot.bean.Plot;
import org.anchoranalysis.plot.bean.colorscheme.GraphColorScheme;

@AllArgsConstructor
public class GraphDualFinderCreator<T> extends ContextualModuleCreator {

    private final GraphFromDualFinderCreator<T> creator;
    private final FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory;
    private final FinderCSVStats finderCSVStats;
    private final GraphColorScheme graphColorScheme;

    @Override
    public NamedModule[] create(
            String namePrefix, AddVideoStatsModuleSupplier adder, VideoStatsModuleGlobalParams mpg)
            throws CreateException {

        ArrayList<NamedModule> outList = new ArrayList<>();

        if (!finderMarksHistory.exists() && !finderCSVStats.exists()) {
            return new NamedModule[] {};
        }

        VideoStatsModuleCreator preferredCreator = null;

        Plot<T> definition = creator.createGraphDefinition(graphColorScheme);

        // Priority given to CSV stats if available
        if (finderCSVStats.exists()) {
            preferredCreator =
                    creator.createGraphModule(
                            namePrefix, definition, finderMarksHistory, finderCSVStats, true);
            VideoStatsModuleCreatorAndAdder creatorAndAdder =
                    new VideoStatsModuleCreatorAndAdder(adder, preferredCreator);
            outList.add(
                    new NamedModule(
                            definition.getTitle(), creatorAndAdder, definition.getShortTitle()));
        }

        // Let's make another
        if (finderMarksHistory.exists()) {
            VideoStatsModuleCreator moduleCreator =
                    creator.createGraphModule(
                            namePrefix, definition, finderMarksHistory, finderCSVStats, false);
            VideoStatsModuleCreatorAndAdder creatorAndAdder =
                    new VideoStatsModuleCreatorAndAdder(adder, moduleCreator);
            outList.add(
                    new NamedModule(
                            String.format("%s (Instance marks data)", definition.getTitle()),
                            creatorAndAdder,
                            definition.getShortTitle()));
        }

        return outList.toArray(new NamedModule[] {});
    }
}
