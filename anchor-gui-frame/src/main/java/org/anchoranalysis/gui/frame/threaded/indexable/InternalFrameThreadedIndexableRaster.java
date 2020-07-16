/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.indexable;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.IndexBridge;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.IGenerateExtraDetail;
import org.anchoranalysis.gui.frame.multiraster.ThreadedIndexedDisplayStackSetter;
import org.anchoranalysis.gui.frame.threaded.stack.InternalFrameThreadedProvider;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.stack.DisplayStack;

public class InternalFrameThreadedIndexableRaster {

    private InternalFrameThreadedProvider delegate;

    private ThreadedIndexedDisplayStackSetter threadedProvider;

    public InternalFrameThreadedIndexableRaster(String frameName) {
        delegate = new InternalFrameThreadedProvider(frameName, false);
    }

    public ControllerPopupMenu controllerPopupMenu() {
        return delegate.controllerPopupMenu();
    }

    public boolean addAdditionalDetails(IGenerateExtraDetail arg0) {
        return delegate.addAdditionalDetails(arg0);
    }

    public ISliderState init(
            BoundedIndexContainer<DisplayStack> cntr,
            DefaultModuleState initialState,
            boolean includeFrameAdjusting,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        threadedProvider = setupProvider(cntr, mpg);

        ISliderState sliderState =
                delegate.init(
                        threadedProvider,
                        cntr,
                        includeFrameAdjusting,
                        initialState,
                        elementRetriever,
                        mpg);
        delegate.setIndexSliderVisible(true);
        return sliderState;
    }

    public IBackgroundSetter backgroundSetter() {
        return threadedProvider;
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    private static ThreadedIndexedDisplayStackSetter setupProvider(
            BoundedIndexContainer<DisplayStack> cntr, VideoStatsModuleGlobalParams mpg)
            throws InitException {
        ThreadedIndexedDisplayStackSetter threadedProvider =
                new ThreadedIndexedDisplayStackSetter();
        threadedProvider.init(
                new IndexBridge<>(cntr), mpg.getThreadPool(), mpg.getLogger().errorReporter());
        return threadedProvider;
    }
}
