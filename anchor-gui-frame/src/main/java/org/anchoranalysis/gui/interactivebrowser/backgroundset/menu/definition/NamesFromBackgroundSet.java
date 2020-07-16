/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

class NamesFromBackgroundSet implements IGetNames {

    private OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet;
    private ErrorReporter errorReporter;

    public NamesFromBackgroundSet(
            OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet,
            ErrorReporter errorReporter) {
        super();
        this.backgroundSet = backgroundSet;
        this.errorReporter = errorReporter;
    }

    @Override
    public List<String> names() {
        try {
            Set<String> namesSorted =
                    new TreeSet<>(backgroundSet.doOperation(ProgressReporterNull.get()).names());
            return new ArrayList<>(namesSorted);

        } catch (Throwable e) {
            errorReporter.recordError(NamesFromBackgroundSet.class, e);
            return new ArrayList<>();
        }
    }
}
