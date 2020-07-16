/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

import javax.swing.JMenu;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinition;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.IImageStackCntrFromName;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

public class ControllerPopupMenuWithBackground {

    private ControllerPopupMenu delegate;
    private IBackgroundSetter backgroundSetter;

    public ControllerPopupMenuWithBackground(
            ControllerPopupMenu delegate, IBackgroundSetter backgroundSetter) {
        super();
        this.delegate = delegate;
        this.backgroundSetter = backgroundSetter;
    }

    public void add(
            IGetNames nameGetter,
            IImageStackCntrFromName stackCntrFromName,
            VideoStatsModuleGlobalParams mpg) {
        BackgroundSetMenu menu =
                new BackgroundSetMenu(
                        nameGetter, stackCntrFromName, backgroundSetter, errorReporter(mpg));

        addAdditionalMenu(menu.getMenu());
    }

    public IBackgroundUpdater add(
            VideoStatsModuleGlobalParams mpg,
            OperationWithProgressReporter<BackgroundSet, GetOperationFailedException>
                    backgroundSet) {
        return addDefinition(mpg, new ChangeableBackgroundDefinitionSimple(backgroundSet));
    }

    public IBackgroundUpdater addDefinition(
            VideoStatsModuleGlobalParams mpg, ChangeableBackgroundDefinition backgroundDefinition) {
        BackgroundSetMenuWithMap menu =
                new BackgroundSetMenuWithMap(
                        backgroundDefinition, backgroundSetter, errorReporter(mpg));

        addAdditionalMenu(menu.getMenu());

        return menu;
    }

    public void addAdditionalMenu(JMenu menu) {
        delegate.addAdditionalMenu(menu);
    }

    public void setRetrieveElementsInPopupEnabled(boolean retrieveElementsInPopupEnabled) {
        delegate.setRetrieveElementsInPopupEnabled(retrieveElementsInPopupEnabled);
    }

    private ErrorReporter errorReporter(VideoStatsModuleGlobalParams mpg) {
        return mpg.getLogger().errorReporter();
    }
}
