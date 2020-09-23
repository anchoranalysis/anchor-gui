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

package org.anchoranalysis.gui.io.loader.manifest.finder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.manifest.FinderCSVStats;
import org.anchoranalysis.gui.manifest.historyfolder.FinderHistoryFolder;
import org.anchoranalysis.gui.manifest.historyfolder.FinderHistoryFolderMarks;
import org.anchoranalysis.gui.videostats.dropdown.MarksHistoryMenu;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.manifest.finder.Finder;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;

@RequiredArgsConstructor
public class FinderMarksWithEnergy implements Finder, ContainerGetter<IndexableMarksWithEnergy> {

    // START REQUIRED ARGUMENTS
    @Getter private final String setName;
    private final String csvStatsName;
    private final String manifestNameMarksHistory;
    private final FinderContext context;
    private final FinderMarksWithEnergy secondary;
    private final FinderMarksWithEnergy tertiary;
    // END REQUIRED ARGUMENTS

    private MarksHistoryMenu dropDown;
    private FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory;

    @Override
    public boolean doFind(ManifestRecorder manifestRecorder) {

        if (context.getMpg().getDefaultColorIndexForMarks() == null) {
            return false;
        }

        // Configurations with Energy
        finderMarksHistory = new FinderHistoryFolderMarks(manifestNameMarksHistory);
        finderMarksHistory.doFind(manifestRecorder);

        // For now we treat both these as obligatory
        if (!finderMarksHistory.exists()) {
            return false;
        }

        // CSV Stats
        final FinderCSVStats finderCSVStats =
                new FinderCSVStats(csvStatsName, context.getMpg().getLogger().errorReporter());
        finderCSVStats.doFind(manifestRecorder);

        dropDown =
                new MarksHistoryMenu(
                        context.getParentMenu(),
                        setName,
                        context.getBoundVideoStats()
                                .createChild(setName)
                                .createAddModuleToMenu(context.getEnergyBackground().getAdder()));
        try {
            dropDown.init(
                    finderMarksHistory,
                    secondary,
                    tertiary,
                    context.getEnergyBackground().getBackground(),
                    finderCSVStats,
                    context.getContext());
        } catch (MenuAddException e) {
            context.getMpg()
                    .getLogger()
                    .errorReporter()
                    .recordError(FinderMarksWithEnergy.class, e);
            return false;
        }
        return true;
    }

    public MarksHistoryMenu get() {
        return dropDown;
    }

    public LoadContainer<IndexableMarksWithEnergy> getHistory() throws OperationFailedException {
        return finderMarksHistory.get();
    }

    public void createDefaultModules() {

        if (dropDown.getCreatorColoredOutline() != null) {
            dropDown.getCreatorColoredOutline()
                    .createVideoStatsModuleForAdder(
                            context.getMpg().getThreadPool(),
                            context.getParentFrame(),
                            context.getMpg().getLogger());
        }
    }

    @Override
    public boolean exists() {
        return dropDown != null;
    }

    @Override
    public BoundedIndexContainer<IndexableMarksWithEnergy> getContainer()
            throws OperationFailedException {
        return getHistory().getContainer();
    }
}
