/* (C)2020 */
package org.anchoranalysis.gui.bean.exporttask;

import javax.swing.ProgressMonitor;

public interface ExportTask {

    boolean hasNecessaryParams(ExportTaskParams params);

    String getBeanName();

    void init();

    boolean execute(ExportTaskParams params, ProgressMonitor progressMonitor)
            throws ExportTaskFailedException;

    int getMinProgress(ExportTaskParams params) throws ExportTaskFailedException;

    int getMaxProgress(ExportTaskParams params) throws ExportTaskFailedException;

    String getOutputName();
}
