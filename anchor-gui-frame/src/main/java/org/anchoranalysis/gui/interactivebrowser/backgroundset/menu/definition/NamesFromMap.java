/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

class NamesFromMap implements IGetNames {

    private OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet;
    private ErrorReporter errorReporter;
    private StringMap map;

    public NamesFromMap(
            StringMap map,
            OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet,
            ErrorReporter errorReporter) {
        super();
        this.backgroundSet = backgroundSet;
        this.errorReporter = errorReporter;
        this.map = map;
    }

    @Override
    public List<String> names() {
        try {
            Set<String> backgroundNames =
                    backgroundSet.doOperation(ProgressReporterNull.get()).names();

            Map<String, String> mapping = map.create();

            return new ArrayList<>(createdSortedSet(mapping, backgroundNames));

        } catch (Throwable e) {
            errorReporter.recordError(NamesFromMap.class, e);
            return new ArrayList<>();
        }
    }

    private static Set<String> createdSortedSet(
            Map<String, String> mapping, Set<String> backgroundNames) {
        // We use tree-set to ensure alphabetical order
        TreeSet<String> namesOut = new TreeSet<>();
        for (String s : mapping.keySet()) {

            if (backgroundNames.contains(mapping.get(s))) {
                namesOut.add(s);
            }
        }
        return namesOut;
    }
}
