/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile;

import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.FileCreatorParams;
import org.anchoranalysis.gui.interactivebrowser.FileOpenManager;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.progressreporter.ProgressReporterInteractiveWorker;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;
import org.apache.commons.lang.time.StopWatch;

public class FileCreatorLoader {

    private FileCreatorParams params;

    private FileOpenManager fileOpenManager;
    private SubgrouppedAdder globalSubgroupAdder;
    private VideoStatsModuleGlobalParams mpg;

    public FileCreatorLoader(
            FileCreatorParams params,
            FileOpenManager fileOpenManager,
            SubgrouppedAdder globalSubgroupAdder) {
        super();
        this.params = params;
        this.mpg = params.getMarkCreatorParams().getModuleParams();
        this.fileOpenManager = fileOpenManager;
        this.globalSubgroupAdder = globalSubgroupAdder;
    }

    public void addFileListSummaryModule(
            List<FileCreator> listFileCreator, Component parentComponent) {
        for (FileCreator fc : listFileCreator) {
            addSingleCreator(fc, parentComponent);
        }
    }

    private void addSingleCreator(FileCreator fileCreator, Component parentComponent) {
        String name = fileCreator.suggestName();

        SwingUtilities.invokeLater(
                () -> {
                    Loader loader = new Loader(new NamedBean<>(name, fileCreator), parentComponent);
                    mpg.getThreadPool()
                            .submitWithProgressMonitor(
                                    loader, String.format("Loading File Creator '%s'", name));
                });
    }

    private class Loader extends InteractiveWorker<Integer, Void> {

        private NamedBean<FileCreator> fileCreator;
        private Component parentComponent;

        public Loader(NamedBean<FileCreator> fileCreator, Component parentComponent) {
            super();
            this.fileCreator = fileCreator;
            this.parentComponent = parentComponent;
        }

        @Override
        protected Integer doInBackground() {
            return 0;
        }

        private VideoStatsModule createModule() {

            StopWatch timer = new StopWatch();
            timer.start();

            String name = fileCreator.getValue().suggestName();

            try (ProgressReporter progressReporter = new ProgressReporterInteractiveWorker(this)) {

                VideoStatsModule module =
                        fileCreator
                                .getValue()
                                .createModule(
                                        name,
                                        params,
                                        mpg,
                                        globalSubgroupAdder.createChild(),
                                        fileOpenManager,
                                        progressReporter);

                mpg.getLogger()
                        .messageLogger()
                        .logFormatted(
                                "Loaded fileListSummaryModule %s (%dms)", name, timer.getTime());

                return module;

            } catch (VideoStatsModuleCreateException e) {
                // Should we change this to the error reporter?
                mpg.getLogger()
                        .messageLogger()
                        .logFormatted(
                                "Failed to load fileListSummaryModule after %s (%dms)",
                                name, timer.getTime());
                mpg.getLogger().errorReporter().recordError(FileCreatorLoader.class, e);

                // + ExceptionUtils.getFullStackTrace(e)
                JOptionPane.showMessageDialog(
                        parentComponent, "An error occurred while loading module. See log.");

                return null;
            }
        }

        @Override
        protected void done() {

            VideoStatsModule module = createModule();
            if (module != null) {
                globalSubgroupAdder.addVideoStatsModule(module);
            }
        }
    }

    public ImporterSettings getImporterSettings() {
        return params.getImporterSettings();
    }
}
