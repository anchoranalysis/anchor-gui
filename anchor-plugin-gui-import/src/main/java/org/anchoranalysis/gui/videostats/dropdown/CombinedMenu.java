/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.JFrame;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskBean;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollection;
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
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException> backgroundSet,
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
                context.getFinderImgStackCollection(),
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
            FinderImgStackCollection finderImgStackCollection,
            ExportTaskList exportTaskList,
            BoundOutputManagerRouteErrors outputManager,
            JFrame parentFrame,
            ErrorReporter errorReporter) {
        VideoStatsOperationMenu exportSubMenu = menu.createSubMenu(name + ": export ", true);

        ExportTaskParams exportTaskParams = new ExportTaskParams();
        exportTaskParams.addFinderCfgNRGHistory(finderFirst);
        exportTaskParams.addFinderCfgNRGHistory(finderSecond);
        exportTaskParams.setFinderImgStackCollection(finderImgStackCollection);
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
