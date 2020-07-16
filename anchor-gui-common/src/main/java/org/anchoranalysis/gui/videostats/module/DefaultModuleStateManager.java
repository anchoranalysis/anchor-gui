/* (C)2020 */
package org.anchoranalysis.gui.videostats.module;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.link.DefaultLinkState;
import org.anchoranalysis.gui.videostats.link.DefaultLinkStateManager;
import org.anchoranalysis.image.stack.DisplayStack;

public class DefaultModuleStateManager {

    private DefaultLinkStateManager linkStateManager;
    private MarkDisplaySettings markDisplaySettings = new MarkDisplaySettings();

    public DefaultModuleStateManager(DefaultLinkState state) {
        this.linkStateManager = new DefaultLinkStateManager(state);
    }

    public DefaultLinkStateManager getLinkStateManager() {
        return linkStateManager;
    }

    // Changing the state here won't change any markDisplay Setitngs
    public DefaultModuleState getState() {
        return new DefaultModuleState(linkStateManager.getState(), markDisplaySettings);
    }

    public DefaultModuleState copy() {
        DefaultModuleState dms =
                new DefaultModuleState(linkStateManager.copy(), markDisplaySettings.duplicate());
        return dms;
    }

    /** Provides a copy of the default module state with a changed background */
    public DefaultModuleState copyChangeBackground(
            FunctionWithException<Integer, DisplayStack, GetOperationFailedException> background) {
        return new DefaultModuleState(
                linkStateManager.copyChangeBackground(background), markDisplaySettings);
    }

    public void setMarkDisplaySettings(MarkDisplaySettings markDisplaySettings) {
        this.markDisplaySettings = markDisplaySettings;
    }
}
