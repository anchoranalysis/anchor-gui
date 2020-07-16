/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IShowError;

public class ToolErrorReporter implements IShowError {

    private IShowError showError;
    private ErrorReporter errorReporter;

    public ToolErrorReporter(IShowError showError, ErrorReporter errorReporter) {
        super();
        this.showError = showError;
        this.errorReporter = errorReporter;
    }

    @Override
    public void showError(String msg) {
        showError.showError(msg);
    }

    public void showError(Class<?> clazz, String msg, String logMsg) {
        showError.showError(msg);
        errorReporter.recordError(clazz, logMsg);
    }

    @Override
    public void clearErrors() {
        showError.clearErrors();
    }

    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }
}
