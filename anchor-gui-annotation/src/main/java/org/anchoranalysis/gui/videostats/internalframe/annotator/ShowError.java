/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IShowError;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;

public class ShowError implements IShowError {

    private PanelNavigation panelNavigation;

    public ShowError() {}

    @Override
    public void showError(String message) {
        if (panelNavigation != null) {
            panelNavigation.setErrorLabelText(message);
        }
    }

    @Override
    public void clearErrors() {
        if (panelNavigation != null) {
            panelNavigation.setErrorLabelText("");
        }
    }

    public void setPanelNavigation(PanelNavigation panelNavigation) {
        this.panelNavigation = panelNavigation;
    }
}
