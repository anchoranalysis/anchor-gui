/* (C)2020 */
package org.anchoranalysis.gui.frame.singleraster;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateRememberStack;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.InternalFrameWithDetailsTopPanel;
import org.anchoranalysis.gui.frame.details.canvas.ControllerFrame;
import org.anchoranalysis.gui.frame.details.canvas.InitialSliderState;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

class InternalFrameNonIndexable {

    private InternalFrameWithDetailsTopPanel delegate;

    public InternalFrameNonIndexable(String frameName) {
        delegate = new InternalFrameWithDetailsTopPanel(frameName);
    }

    public ISliderState init(
            IDisplayUpdateRememberStack stackProvider,
            int initialSliceNum,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        return delegate.init(
                new NullBounds(),
                new ZeroIndexGetter(),
                stackProvider,
                new InitialSliderState(false, 0, initialSliceNum, false),
                elementRetriever,
                mpg);
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public ControllerPopupMenu controllerPopupMenu() {
        return delegate.controllerPopupMenu();
    }

    public ControllerFrame controllerFrame() {
        return delegate.controllerAction().frame();
    }
}
