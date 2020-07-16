/* (C)2020 */
package org.anchoranalysis.gui.videostats.operation;

import java.awt.event.ActionListener;
import java.util.Optional;
import javax.swing.JFrame;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.bean.exporttask.ExportTask;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskActionAsThread;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskActionAsThread.ExportTaskCommand;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

public class VideoStatsOperationFromExportTask implements VideoStatsOperation {

    private ActionListener actionListenerWithMessages;
    private ExportTask exportTask;

    private final ExportTaskCommand commandWithMessage;
    private final ExportTaskCommand commandWithoutMessage;

    public VideoStatsOperationFromExportTask(
            ExportTask exportTask,
            ExportTaskParams exportTaskParams,
            JFrame parentFrame,
            ErrorReporter errorReporter) {

        this.exportTask = exportTask;

        commandWithMessage =
                new ExportTaskCommand(
                        exportTask, exportTaskParams, parentFrame, true, errorReporter);
        commandWithoutMessage =
                new ExportTaskCommand(
                        exportTask, exportTaskParams, parentFrame, false, errorReporter);

        actionListenerWithMessages = new ExportTaskActionAsThread(commandWithMessage);
    }

    @Override
    public String getName() {
        return exportTask.getOutputName();
    }

    @Override
    public void execute(boolean withMessages) {
        if (withMessages == true) {
            actionListenerWithMessages.actionPerformed(null);
        } else {
            commandWithoutMessage.doTask();
        }
    }

    @Override
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return Optional.empty();
    }
}
