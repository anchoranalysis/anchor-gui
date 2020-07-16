/* (C)2020 */
package org.anchoranalysis.gui.bean.exporttask;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;

public class ExportTaskActionAsThread extends AbstractAction {

    private static final long serialVersionUID = -3944294960260017562L;

    private transient ExportTaskCommand command;

    public static class ExportTaskCommand {
        private ExportTask exportTask;
        private ExportTaskParams exportTaskParams;
        private JFrame parentFrame;
        private boolean showMessageBoxes;
        private ErrorReporter errorReporter;

        public ExportTaskCommand(
                ExportTask exportTask,
                ExportTaskParams exportTaskParams,
                JFrame parentFrame,
                boolean showMessageBoxes,
                ErrorReporter errorReporter) {
            super();
            this.exportTask = exportTask;
            this.exportTaskParams = exportTaskParams;
            this.parentFrame = parentFrame;
            this.showMessageBoxes = showMessageBoxes;
            this.errorReporter = errorReporter;
        }

        public void doTask() {

            String exportTaskTitle = "Export Task: " + exportTask.getBeanName();

            try {

                exportTask.init();

                int min = exportTask.getMinProgress(exportTaskParams);

                int max = exportTask.getMaxProgress(exportTaskParams);

                final ProgressMonitor progressMonitor =
                        new ProgressMonitor(
                                parentFrame, "Exporting", exportTask.getOutputName(), min, max);

                if (exportTask.execute(exportTaskParams, progressMonitor)) {

                    if (showMessageBoxes) {
                        JOptionPane.showMessageDialog(
                                parentFrame,
                                String.format(
                                        "Export task '%s' succeeded", exportTask.getOutputName()),
                                exportTaskTitle,
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {

                    if (showMessageBoxes) {
                        JOptionPane.showMessageDialog(
                                parentFrame,
                                String.format(
                                        "Export task '%s' cancelled", exportTask.getOutputName()),
                                exportTaskTitle,
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (ExportTaskFailedException e) {
                recordException(e, exportTaskTitle);
            }
        }

        private void recordException(Exception e, String exportTaskTitle) {
            String newLine = System.getProperty("line.separator");
            if (showMessageBoxes) {
                JOptionPane.showMessageDialog(
                        null,
                        String.format(
                                "Export task '%s' failed: %s%s",
                                exportTask.getOutputName(), newLine, e.toString()),
                        exportTaskTitle,
                        JOptionPane.ERROR_MESSAGE);
            } else {
                errorReporter.recordError(ExportTaskCommand.class, e);
            }
        }

        public ExportTask getExportTask() {
            return exportTask;
        }
    }

    public ExportTaskActionAsThread(ExportTaskCommand command) {
        super(command.getExportTask().getOutputName());
        this.command = command;
    }

    public ExportTaskActionAsThread(String label, ExportTaskCommand command) {
        super(label);
        this.command = command;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

        Thread thread = new TaskThread(command);
        thread.start();
    }

    private static class TaskThread extends Thread {

        private ExportTaskCommand command;

        public TaskThread(ExportTaskCommand command) {
            super();
            this.command = command;
        }

        @Override
        public void run() {
            command.doTask();
        }
    }
}
