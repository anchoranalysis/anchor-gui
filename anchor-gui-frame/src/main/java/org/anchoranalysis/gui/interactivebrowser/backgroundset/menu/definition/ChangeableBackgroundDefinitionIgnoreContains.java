/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

@RequiredArgsConstructor
public class ChangeableBackgroundDefinitionIgnoreContains extends ChangeableBackgroundDefinition {

    // START REQUIRED ARGUMENTS
    private final ChangeableBackgroundDefinition background;
    private final String contains;
    // END REQUIRED ARGUMENTS

    @Override
    public void update(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        background.update(backgroundSet);
    }

    @Override
    public IImageStackCntrFromName stackCntrFromName(ErrorReporter errorReporter) {
        return background.stackCntrFromName(errorReporter);
    }

    @Override
    public IGetNames names(ErrorReporter errorReporter) {
        IGetNames namesGet = background.names(errorReporter);
        return () -> filterList(namesGet.names());
    }

    private List<String> filterList(List<String> list) {
        return FunctionalList.filterToList(list, a -> !a.contains(contains));
    }
}
