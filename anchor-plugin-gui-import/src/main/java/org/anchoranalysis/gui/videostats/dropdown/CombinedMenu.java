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

import javax.swing.JFrame;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacks;
import org.anchoranalysis.gui.io.loader.manifest.finder.CfgNRGFinderContext;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCfgNRGSet;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromExportTask;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;

public class CombinedMenu {

    private IAddModuleToMenu adder;
    private VideoStatsOperationMenu menu;

    public CombinedMenu(VideoStatsOperationMenu parentMenu, IAddModuleToMenu adder) {
        this.adder = adder;
        this.menu = parentMenu.createSubMenu("Combined", true);
    }

    public void addCombination(
            FinderCfgNRGSet finderFirst,
            FinderCfgNRGSet finderSecond,
            CheckedProgressingSupplier<BackgroundSet, BackgroundStackContainerException>
                    backgroundSet,
            CfgNRGFinderContext context)
            throws MenuAddException {

        if (!finderFirst.exists() || !finderSecond.exists()) {
            return;
        }

        final String combinationName =
                String.format("%s vs %s", finderFirst.getSetName(), finderSecond.getSetName());

        ContextualModuleCreator mc =
                new SingleContextualModuleCreator(
                        new CombinationCreator(
                                finderFirst, finderSecond, combinationName, backgroundSet));

        adder.addModuleToMenu(menu, mc, false, context.getMpg());

        addExportTasks(
                combinationName,
                finderFirst,
                finderSecond,
                context.getFinderStacks(),
                context.getMpg().getExportTaskList(),
                context.getOutputManager(),
                context.getParentFrame(),
                context.getMpg().getLogger().errorReporter());
    }

    public VideoStatsOperationMenu getMenu() {
        return menu;
    }

    private void addExportTasks(
            String name,
            final FinderCfgNRGSet finderFirst,
            final FinderCfgNRGSet finderSecond,
            FinderStacks finderStacks,
            ExportTaskList exportTaskList,
            BoundOutputManagerRouteErrors outputManager,
            JFrame parentFrame,
            ErrorReporter errorReporter) {
        VideoStatsOperationMenu exportSubMenu = menu.createSubMenu(name + ": export ", true);

        ExportTaskParams exportTaskParams = new ExportTaskParams();
        exportTaskParams.addFinderCfgNRGHistory(finderFirst);
        exportTaskParams.addFinderCfgNRGHistory(finderSecond);
        exportTaskParams.setFinderStacks(finderStacks);
        exportTaskParams.setOutputManager(outputManager);

        // TODO, HACK, as the RGB creator requires this
        try {
            exportTaskParams.setColorIndexMarks(
                    outputManager.getOutputWriteSettings().genDefaultColorIndex(3));
        } catch (OperationFailedException e) {
            errorReporter.recordError(CombinedMenu.class, e);
        }

        for (ExportTaskBean exportTask : exportTaskList) {

            if (exportTask.hasNecessaryParams(exportTaskParams)) {

                try {
                    ExportTaskBean exportTaskDup = exportTask.duplicateBean();

                    exportSubMenu.add(
                            new VideoStatsOperationFromExportTask(
                                    exportTaskDup, exportTaskParams, parentFrame, errorReporter));

                } catch (BeanDuplicateException e) {
                    errorReporter.recordError(CombinedMenu.class, e);
                }
            }
        }
    }
}
