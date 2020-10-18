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

package org.anchoranalysis.gui.videostats.operation;

import java.awt.event.ActionListener;
import java.util.Optional;
import javax.swing.JFrame;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.export.bean.ExportTask;
import org.anchoranalysis.gui.export.bean.ExportTaskActionAsThread;
import org.anchoranalysis.gui.export.bean.ExportTaskActionAsThread.ExportTaskCommand;
import org.anchoranalysis.gui.export.bean.ExportTaskParams;
import org.anchoranalysis.gui.videostats.operation.combine.VideoStatsOperationCombine;

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
    public Optional<VideoStatsOperationCombine> getCombiner() {
        return Optional.empty();
    }
}
