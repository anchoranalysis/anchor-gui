/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;

public abstract class ExecuteWhenMenuFirstSelected implements MenuListener {

    private boolean first = true;

    private ErrorReporter errorReporter;

    public ExecuteWhenMenuFirstSelected(ErrorReporter errorReporter) {
        super();
        this.errorReporter = errorReporter;
    }

    @Override
    public void menuSelected(MenuEvent arg0) {

        if (first) {
            try {
                execute();
            } catch (OperationFailedException e) {
                errorReporter.recordError(ExecuteWhenMenuFirstSelected.class, e);
            }
            first = false;
        }
    }

    @Override
    public void menuDeselected(MenuEvent arg0) {}

    @Override
    public void menuCanceled(MenuEvent arg0) {}

    public abstract void execute() throws OperationFailedException;
}
