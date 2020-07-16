/* (C)2020 */
package org.anchoranalysis.gui.cfgnrg;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;

public class StatePanelFrameHistoryCfgNRGInstantState {

    private StatePanelFrameHistory<CfgNRGInstantState> delegate;

    // private EventListenerList eventListeners = new EventListenerList();

    public StatePanelFrameHistoryCfgNRGInstantState(String title, boolean includeFrameAdjusting) {
        super();
        this.delegate = new StatePanelFrameHistory<>(title, includeFrameAdjusting);
    }

    // This is separated from the constructor, so we can set up all the event handlers before
    // calling init, so an event
    //   can be triggered for the initial state
    public void init(
            int initialIndex,
            LoadContainer<CfgNRGInstantState> selectedHistory,
            StatePanel<CfgNRGInstantState> tablePanel,
            ErrorReporter errorReporter)
            throws InitException {
        this.delegate.init(initialIndex, selectedHistory, tablePanel, errorReporter);
    }

    public void setFrameSliderVisible(boolean visibility) {
        delegate.setFrameSliderVisible(visibility);
    }

    public ControllerSize controllerSize() {
        return delegate.controllerSize();
    }

    public IModuleCreatorDefaultState moduleCreator() {
        return delegate.moduleCreator();
    }
}
