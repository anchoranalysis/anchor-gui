/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.launch;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.TaskWithoutSharedState;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.gui.interactivebrowser.browser.InteractiveBrowser;
import org.anchoranalysis.gui.interactivebrowser.input.InteractiveBrowserInput;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;

public class InteractiveBrowserTask extends TaskWithoutSharedState<InteractiveBrowserInput> {

    // START BEAN PROPERTIES
    @BeanField private ExportTaskList exportTaskList;
    // END BEAN PROPERTIES

    @Override
    public InputTypesExpected inputTypesExpected() {
        return new InputTypesExpected(InteractiveBrowserInput.class);
    }

    @Override
    public void doJobOnInputObject(InputBound<InteractiveBrowserInput, NoSharedState> params)
            throws JobExecutionException {

        try {
            InteractiveBrowser browser =
                    new InteractiveBrowser(params.context(), getExportTaskList());
            browser.init(params.getInputObject());
            browser.showWithDefaultView();
        } catch (InitException e) {
            throw new JobExecutionException(e);
        }
    }

    @Override
    public boolean hasVeryQuickPerInputExecution() {
        return true;
    }

    public ExportTaskList getExportTaskList() {
        return exportTaskList;
    }

    public void setExportTaskList(ExportTaskList exportTaskList) {
        this.exportTaskList = exportTaskList;
    }
}
