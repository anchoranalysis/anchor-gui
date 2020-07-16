/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.image.stack.DisplayStack;

class StackFromBackgroundSet implements IImageStackCntrFromName {

    private OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet;
    private ErrorReporter errorReporter;

    public StackFromBackgroundSet(
            OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet,
            ErrorReporter errorReporter) {
        super();
        this.backgroundSet = backgroundSet;
        this.errorReporter = errorReporter;
    }

    @Override
    public FunctionWithException<Integer, DisplayStack, GetOperationFailedException>
            imageStackCntrFromName(String name) {
        try {
            return backgroundSet.doOperation(ProgressReporterNull.get()).stackCntr(name);
        } catch (Throwable e) {
            errorReporter.recordError(NamesFromBackgroundSet.class, e);
            return null;
        }
    }
}
