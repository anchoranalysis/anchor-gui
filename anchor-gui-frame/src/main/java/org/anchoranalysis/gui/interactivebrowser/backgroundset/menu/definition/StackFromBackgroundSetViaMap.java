/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.Map;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.image.stack.DisplayStack;

class StackFromBackgroundSetViaMap implements IImageStackCntrFromName {

    private OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet;
    private Map<String, String> map;
    private ErrorReporter errorReporter;

    public StackFromBackgroundSetViaMap(
            StringMap map,
            OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet,
            ErrorReporter errorReporter) {
        super();
        this.backgroundSet = backgroundSet;
        this.map = map.create();
        this.errorReporter = errorReporter;
    }

    @Override
    public FunctionWithException<Integer, DisplayStack, GetOperationFailedException>
            imageStackCntrFromName(String name) throws GetOperationFailedException {
        try {
            return backgroundSet.doOperation(ProgressReporterNull.get()).stackCntr(map.get(name));
        } catch (Throwable e) {
            errorReporter.recordError(NamesFromBackgroundSet.class, e);
            return null;
        }
    }
}
