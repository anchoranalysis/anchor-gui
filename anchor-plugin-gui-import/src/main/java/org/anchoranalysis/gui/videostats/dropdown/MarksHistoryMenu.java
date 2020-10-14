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

package org.anchoranalysis.gui.videostats.dropdown;

import com.google.common.base.Preconditions;
import java.io.IOException;
import javax.swing.JFrame;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.export.bean.ExportTaskBean;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.gui.export.bean.task.ExportTaskList;
import org.anchoranalysis.gui.finder.FinderCSVStats;
import org.anchoranalysis.gui.finder.FinderHistoryFolder;
import org.anchoranalysis.gui.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacks;
import org.anchoranalysis.gui.io.loader.manifest.finder.MarksWithEnergyFinderContext;
import org.anchoranalysis.gui.plot.bean.BarPlotEnergyBreakdown;
import org.anchoranalysis.gui.plot.creator.BridgedPlotEnergyCreator;
import org.anchoranalysis.gui.plot.creator.BridgedPlotNumberMarksCreator;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyBackground;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.EnergyTableCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.ColoredOutlineCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.GraphDualFinderCreator;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.PlotCSVStatistic;
import org.anchoranalysis.gui.videostats.dropdown.modulecreator.graph.PlotEnergyBreakdown;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromExportTask;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.io.manifest.finder.FinderSerializedObject;
import org.anchoranalysis.io.output.outputter.InputOutputContext;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.feature.energy.marks.VoxelizedMarksWithEnergy;
import org.anchoranalysis.mpp.segment.bean.kernel.proposer.KernelProposer;
import org.anchoranalysis.plot.bean.Plot;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.AllKernelAccptCSVStatistic;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsKernelAccptProb;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsKernelProp;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsTemperature;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsTime;
import org.anchoranalysis.plugin.gui.bean.graphdefinition.line.GraphDefinitionLineIterVsTimePerIter;

// TODO needs heavy refactoring for readability
public class MarksHistoryMenu {

    private final IAddModuleToMenu adder;

    // Exposed creators
    private VideoStatsModuleCreatorAndAdder creatorEnergyTable = null;
    private VideoStatsModuleCreatorAndAdder creatorColoredOutline = null;

    private VideoStatsOperationMenu menu;

    public MarksHistoryMenu(
            VideoStatsOperationMenu parentMenu, String name, IAddModuleToMenu adder) {
        this.adder = adder;
        this.menu = parentMenu.createSubMenu(name, true);
    }

    public void init(
            FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory,
            ContainerGetter<IndexableMarksWithEnergy>
                    finderSecondaryHistory, // Can be null if there's no secondary
            ContainerGetter<IndexableMarksWithEnergy>
                    finderTertiaryHistory, // Can be null if there's no secondary
            EnergyBackground energyBackground,
            FinderCSVStats finderCSVStats,
            MarksWithEnergyFinderContext context)
            throws MenuAddException {

        Preconditions.checkArgument(finderMarksHistory.exists());
        Preconditions.checkArgument(context.getFinderStacks().exists());

        // Colored Outline
        creatorColoredOutline =
                adder.addModuleToMenu(
                        menu,
                        new SingleContextualModuleCreator(
                                new ColoredOutlineCreator(
                                        finderMarksHistory, energyBackground.getBackgroundSet())),
                        false,
                        context.getMpg());

        if (energyBackground.getEnergyStack() != null) {
            // Energy Table
            creatorEnergyTable =
                    adder.addModuleToMenu(
                            menu,
                            new SingleContextualModuleCreator(
                                    new EnergyTableCreator(
                                            finderMarksHistory::get,
                                            energyBackground.getEnergyStack(),
                                            context.getMpg().getDefaultColorIndexForMarks())),
                            false,
                            context.getMpg());
        }

        menu.addSeparator();

        addGraphs(
                finderMarksHistory,
                context.getFinderKernelProposer(),
                finderCSVStats,
                context.getMpg());

        addExportTasks(
                finderMarksHistory,
                context.getFinderStacks(),
                finderSecondaryHistory,
                finderTertiaryHistory,
                finderCSVStats,
                context.getMpg().getDefaultColorIndexForMarks(),
                context.getMpg().getExportTaskList(),
                context.getParentFrame(),
                context.getInputOutputContext());
    }

    private void addExportTasks(
            FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory,
            FinderStacks finderStacks,
            ContainerGetter<IndexableMarksWithEnergy> finderSecondaryHistory,
            ContainerGetter<IndexableMarksWithEnergy> finderTertiaryHistory,
            FinderCSVStats finderCSVStats,
            ColorIndex colorIndex,
            ExportTaskList exportTaskList,
            JFrame parentFrame,
            InputOutputContext inputOutputContext) {
        VideoStatsOperationMenu exportSubMenu = menu.createSubMenu("Export", true);

        ExportTaskParams exportTaskParams = new ExportTaskParams(inputOutputContext);
        exportTaskParams.setColorIndexMarks(colorIndex);
        exportTaskParams.addFinderMarksHistory(finderMarksHistory);
        exportTaskParams.setFinderStacks(finderStacks);
        exportTaskParams.setFinderCsvStatistics(finderCSVStats);
        exportTaskParams.addFinderMarksHistory(finderSecondaryHistory);
        exportTaskParams.addFinderMarksHistory(finderTertiaryHistory);

        for (ExportTaskBean exportTask : exportTaskList) {

            if (exportTask.hasNecessaryParams(exportTaskParams)) {

                try {
                    ExportTaskBean exportTaskDup = exportTask.duplicateBean();

                    exportSubMenu.add(
                            new VideoStatsOperationFromExportTask(
                                    exportTaskDup, exportTaskParams, parentFrame, inputOutputContext.getErrorReporter()));

                } catch (BeanDuplicateException e) {
                    inputOutputContext.getErrorReporter().recordError(MarksHistoryMenu.class, e);
                }
            }
        }
    }

    private void addGraphs(
            final FinderHistoryFolder<IndexableMarksWithEnergy> finderMarksHistory,
            final FinderSerializedObject<KernelProposer<VoxelizedMarksWithEnergy>>
                    finderKernelProposer,
            final FinderCSVStats finderCSVStats,
            final VideoStatsModuleGlobalParams mpg)
            throws MenuAddException {
        final VideoStatsOperationMenu graphSubMenu = menu.createSubMenu("Graphs", false);

        // We only add these items after the item has been selected, as it can be time consuming to
        // do
        //  the finderKernelProposer.get()
        graphSubMenu.addMenuListener(
                new ExecuteWhenMenuFirstSelected(mpg.getLogger().errorReporter()) {

                    @Override
                    public void execute() throws OperationFailedException {
                        try {
                            adder.addModuleToMenu(
                                    graphSubMenu,
                                    new GraphDualFinderCreator<>(
                                            new BridgedPlotEnergyCreator(),
                                            finderMarksHistory,
                                            finderCSVStats,
                                            mpg.getGraphColorScheme()),
                                    false,
                                    mpg);
                            adder.addModuleToMenu(
                                    graphSubMenu,
                                    new GraphDualFinderCreator<>(
                                            new BridgedPlotNumberMarksCreator(),
                                            finderMarksHistory,
                                            finderCSVStats,
                                            mpg.getGraphColorScheme()),
                                    false,
                                    mpg);

                            addCSVStatistic(
                                    graphSubMenu,
                                    new GraphDefinitionLineIterVsTemperature(
                                            mpg.getGraphColorScheme()),
                                    false);
                            addCSVStatistic(
                                    graphSubMenu,
                                    new GraphDefinitionLineIterVsTime(mpg.getGraphColorScheme()),
                                    false);
                            addCSVStatistic(
                                    graphSubMenu,
                                    new GraphDefinitionLineIterVsTimePerIter(
                                            mpg.getGraphColorScheme()),
                                    false);
                        } catch (MenuAddException e) {
                            throw new OperationFailedException(e);
                        }

                        if (finderKernelProposer.exists() && finderCSVStats.exists()) {

                            VideoStatsOperationMenu acceptedSubMenu =
                                    graphSubMenu.createSubMenu(
                                            "Kernel - Rate of Acceptance", false);

                            String[] kernelNames;
                            try {
                                KernelProposer<VoxelizedMarksWithEnergy> kp =
                                        finderKernelProposer.get();

                                // TODO could be called many times, consider better strategy
                                kp.init();
                                kernelNames = kp.createKernelFactoryNames();
                                addCSVStatistic(
                                        acceptedSubMenu,
                                        new GraphDefinitionLineIterVsKernelAccptProb(
                                                "multiple series",
                                                kernelNames,
                                                new AllKernelAccptCSVStatistic(),
                                                mpg.getGraphColorScheme()),
                                        true);
                                acceptedSubMenu.addSeparator();
                            } catch (MenuAddException | InitException | IOException e) {
                                throw new OperationFailedException(e);
                            }

                            try {
                                for (int i = 0;
                                        i < finderKernelProposer.get().getNumberKernels();
                                        i++) {
                                    addCSVStatistic(
                                            acceptedSubMenu,
                                            new GraphDefinitionLineIterVsKernelAccptProb(
                                                    finderKernelProposer.get(),
                                                    i,
                                                    mpg.getGraphColorScheme()),
                                            true);
                                }
                            } catch (MenuAddException | IOException e) {
                                throw new OperationFailedException(e);
                            }
                        }

                        if (finderKernelProposer.exists() && finderCSVStats.exists()) {

                            VideoStatsOperationMenu propSubMenu =
                                    graphSubMenu.createSubMenu("Kernel - Rate of Proposal", false);
                            try {
                                for (int i = 0;
                                        i < finderKernelProposer.get().getNumberKernels();
                                        i++) {
                                    addCSVStatistic(
                                            propSubMenu,
                                            new GraphDefinitionLineIterVsKernelProp(
                                                    finderKernelProposer.get(),
                                                    i,
                                                    mpg.getGraphColorScheme()),
                                            true);
                                }
                            } catch (MenuAddException | IOException e) {
                                throw new OperationFailedException(e);
                            }
                        }
                    }

                    private void addCSVStatistic(
                            VideoStatsOperationMenu subMenu,
                            Plot<CSVStatistic> graphDefinition,
                            boolean useShortNames)
                            throws MenuAddException {
                        adder.addModuleToMenu(
                                subMenu,
                                new SingleContextualModuleCreator(
                                        new PlotCSVStatistic(graphDefinition, finderCSVStats)),
                                useShortNames,
                                mpg);
                    }
                });

        try {
            BarPlotEnergyBreakdown graphEnergyBreakdown = new BarPlotEnergyBreakdown();
            graphEnergyBreakdown.setGraphColorScheme(mpg.getGraphColorScheme());
            adder.addModuleToMenu(
                    graphSubMenu,
                    new SingleContextualModuleCreator(
                            new PlotEnergyBreakdown(
                                    graphEnergyBreakdown,
                                    finderMarksHistory,
                                    mpg.getDefaultColorIndexForMarks())),
                    false,
                    mpg);
        } catch (InitException e) {
            throw new MenuAddException(e);
        }
    }

    public VideoStatsModuleCreatorAndAdder getCreatorEnergyTable() {
        return creatorEnergyTable;
    }

    public VideoStatsModuleCreatorAndAdder getCreatorColoredOutline() {
        return creatorColoredOutline;
    }

    public VideoStatsOperationMenu getMenu() {
        return menu;
    }
}
