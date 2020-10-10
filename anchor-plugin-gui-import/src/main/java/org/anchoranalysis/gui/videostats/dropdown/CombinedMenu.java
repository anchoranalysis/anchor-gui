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
import org.anchoranalysis.gui.export.bean.ExportTaskBean;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.gui.export.bean.task.ExportTaskList;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacks;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderMarksWithEnergy;
import org.anchoranalysis.gui.io.loader.manifest.finder.MarksWithEnergyFinderContext;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.ContextualModuleCreator;
import org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator.SingleContextualModuleCreator;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationFromExportTask;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperationMenu;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

public class CombinedMenu {

    private IAddModuleToMenu adder;
    private VideoStatsOperationMenu menu;

    public CombinedMenu(VideoStatsOperationMenu parentMenu, IAddModuleToMenu adder) {
        this.adder = adder;
        this.menu = parentMenu.createSubMenu("Combined", true);
    }

    public void addCombination(
            FinderMarksWithEnergy finderFirst,
            FinderMarksWithEnergy finderSecond,
            BackgroundSetProgressingSupplier backgroundSet,
            MarksWithEnergyFinderContext context)
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
                context.getParentFrame(),
                context.getInputOutputContext());
    }

    public VideoStatsOperationMenu getMenu() {
        return menu;
    }

    private void addExportTasks(
            String name,
            final FinderMarksWithEnergy finderFirst,
            final FinderMarksWithEnergy finderSecond,
            FinderStacks finderStacks,
            ExportTaskList exportTaskList,
            JFrame parentFrame,
            InputOutputContext context) {
        VideoStatsOperationMenu exportSubMenu = menu.createSubMenu(name + ": export ", true);

        ExportTaskParams exportTaskParams = new ExportTaskParams(context);
        exportTaskParams.addFinderMarksHistory(finderFirst);
        exportTaskParams.addFinderMarksHistory(finderSecond);
        exportTaskParams.setFinderStacks(finderStacks);

        // TODO, HACK, as the RGB creator requires this
        try {
            exportTaskParams.setColorIndexMarks(context.getOutputter().getSettings().defaultColorIndexFor(3));
        } catch (OperationFailedException e) {
            context.getErrorReporter().recordError(CombinedMenu.class, e);
        }

        for (ExportTaskBean exportTask : exportTaskList) {

            if (exportTask.hasNecessaryParams(exportTaskParams)) {

                try {
                    ExportTaskBean exportTaskDup = exportTask.duplicateBean();

                    exportSubMenu.add(
                            new VideoStatsOperationFromExportTask(
                                    exportTaskDup, exportTaskParams, parentFrame, context.getErrorReporter()));

                } catch (BeanDuplicateException e) {
                    context.getErrorReporter().recordError(CombinedMenu.class, e);
                }
            }
        }
    }
}
