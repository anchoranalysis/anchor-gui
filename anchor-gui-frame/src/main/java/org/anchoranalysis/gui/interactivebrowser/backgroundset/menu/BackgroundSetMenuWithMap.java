/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

import javax.swing.JMenu;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;

class BackgroundSetMenuWithMap implements IBackgroundUpdater {

    private BackgroundSetMenu delegate;
    private ErrorReporter errorReporter;
    private ChangeableBackgroundDefinition backgroundDefinition;

    public BackgroundSetMenuWithMap(
            ChangeableBackgroundDefinition backgroundDefinition,
            IBackgroundSetter backgroundSetter,
            ErrorReporter errorReporter) {
        delegate =
                new BackgroundSetMenu(
                        backgroundDefinition.names(errorReporter),
                        backgroundDefinition.stackCntrFromName(errorReporter),
                        backgroundSetter,
                        errorReporter);
        this.errorReporter = errorReporter;
        this.backgroundDefinition = backgroundDefinition;
    }

    @Override
    public void update(
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        backgroundDefinition.update(backgroundSet);
        delegate.update(
                backgroundDefinition.names(errorReporter),
                backgroundDefinition.stackCntrFromName(errorReporter));
    }

    public JMenu getMenu() {
        return delegate.getMenu();
    }
}
