/*-
 * #%L
 * anchor-gui-frame
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

import java.awt.Component;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.core.progress.ProgressReporterOneOfMany;
import org.anchoranalysis.gui.progressreporter.ProgressReporterInteractiveWorker;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;

public class VideoStatsModuleCreatorAndAdder {

    private CheckedProgressingSupplier<AddVideoStatsModule, ? extends Throwable> adderOperation;
    private VideoStatsModuleCreator creator;

    public VideoStatsModuleCreatorAndAdder(
            CheckedProgressingSupplier<AddVideoStatsModule, ? extends Throwable> adderOperation,
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

    private class Worker extends InteractiveWorker<AddVideoStatsModule, Void> {

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
        protected AddVideoStatsModule doInBackground() {

            exceptionRecorded = null;

            try {
                try (ProgressReporter progressReporter =
                        new ProgressReporterInteractiveWorker(this)) {

                    try (ProgressReporterMultiple prm =
                            new ProgressReporterMultiple(progressReporter, 2)) {
                        AddVideoStatsModule adder =
                                adderOperation.get(new ProgressReporterOneOfMany(prm));
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
