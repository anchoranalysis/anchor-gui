/* (C)2020 */
package org.anchoranalysis.gui.videostats.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JInternalFrame;

// A thread pool for all SwingWorkers created at any given time
public class InteractiveThreadPool {

    private ExecutorService executor;
    private AddProgressBarInternalFrame internalFrameAdder;

    public static interface AddProgressBarInternalFrame {
        void addInternalFrame(JInternalFrame frame);
    }

    public InteractiveThreadPool(AddProgressBarInternalFrame internalFrameAdder) {
        this.internalFrameAdder = internalFrameAdder;
        executor = Executors.newCachedThreadPool();
    }

    public void submit(InteractiveWorker<?, ?> sw, String name) {

        // System.out.printf("SUBMIT: %s\n", name );
        // sw.execute();

        executor.submit(sw);
    }

    public void submitWithProgressMonitor(final InteractiveWorker<?, ?> sw, String name) {

        // System.out.printf("SUBMIT: %s\n", name );

        ProgressBarInternalFrame progressBarFrame = new ProgressBarInternalFrame(name);

        internalFrameAdder.addInternalFrame(progressBarFrame.getInternalFrame());
        sw.addPropertyChangeListener(progressBarFrame.createChangeListener(sw));

        executor.submit(sw);
    }
}
