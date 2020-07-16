/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import java.awt.Component;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.gui.progressreporter.ProgressReporterInteractiveWorker;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;

public class VideoStatsModuleCreatorAndAdder {

    private OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adderOperation;
    private VideoStatsModuleCreator creator;

    public VideoStatsModuleCreatorAndAdder(
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adderOperation,
            VideoStatsModuleCreator creator) {
        this.adderOperation = adderOperation;
        this.creator = creator;
    }

    public void createVideoStatsModuleForAdder(
            InteractiveThreadPool threadPool, Component parentComponent, Logger logger) {
        Worker worker = new Worker(logger);
        worker.beforeBackground(parentComponent);
        threadPool.submitWithProgressMonitor(worker, "Create module");
    }

    private class Worker extends InteractiveWorker<IAddVideoStatsModule, Void> {

        private Throwable exceptionRecorded;
        private Logger logger;

        public Worker(Logger logger) {
            super();
            this.logger = logger;
        }

        public void beforeBackground(Component parentComponent) {
            creator.beforeBackground(parentComponent);
        }

        @Override
        protected IAddVideoStatsModule doInBackground() {

            exceptionRecorded = null;

            try {
                try (ProgressReporter progressReporter =
                        new ProgressReporterInteractiveWorker(this)) {

                    try (ProgressReporterMultiple prm =
                            new ProgressReporterMultiple(progressReporter, 2)) {
                        IAddVideoStatsModule adder =
                                adderOperation.doOperation(new ProgressReporterOneOfMany(prm));
                        prm.incrWorker();

                        creator.doInBackground(new ProgressReporterOneOfMany(prm));

                        return adder;
                    }
                }
            } catch (Throwable e) {
                exceptionRecorded = e;
                return null;
            }
        }

        @Override
        protected void done() {

            if (exceptionRecorded != null) {
                displayErrorDialog(exceptionRecorded);
                return;
            }

            try {
                creator.createAndAddVideoStatsModule(get());
            } catch (InterruptedException
                    | ExecutionException
                    | VideoStatsModuleCreateException e) {
                displayErrorDialog(e);
            }
        }

        private void displayErrorDialog(Throwable e) {

            logger.errorReporter().recordError(VideoStatsModuleCreatorAndAdder.class, e);

            // custom title, error icon
            JOptionPane.showMessageDialog(
                    null,
                    "An error occurred creating the module. Please see log file.",
                    "An error occurred creating the module",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public VideoStatsModuleCreator getCreator() {
        return creator;
    }
}
