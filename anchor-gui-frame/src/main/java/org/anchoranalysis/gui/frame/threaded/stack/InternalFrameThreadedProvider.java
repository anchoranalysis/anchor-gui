/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.stack;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.IGenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.InternalFrameWithDetailsTopPanel;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InitialSliderState;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.extent.ImageDimensions;

public class InternalFrameThreadedProvider {

    private InternalFrameWithDetailsTopPanel delegate;

    private boolean indexesAreFrames = false;

    private IThreadedProducer producer;

    public InternalFrameThreadedProvider(String title, boolean indexesAreFrames) {
        delegate = new InternalFrameWithDetailsTopPanel(title);
        this.indexesAreFrames = indexesAreFrames;
    }

    public ISliderState init(
            IThreadedProducer producer,
            BoundedRangeIncompleteDynamic indexBounds,
            boolean includeFrameAdjusting,
            DefaultModuleState initialState,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        this.producer = producer;

        // We assume all channels have the same number of slices
        return delegate.init(
                indexBounds,
                producer.getIndexGettableSettable(),
                producer.getStackProvider(),
                new InitialSliderState(
                        includeFrameAdjusting,
                        defaultIndex(initialState),
                        initialState.getLinkState().getSliceNum(),
                        true),
                elementRetriever,
                mpg);
    }

    public int defaultIndex(DefaultModuleState initialState) {
        return indexesAreFrames ? initialState.getLinkState().getFrameIndex() : 0;
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return defaultFrameState -> {
            VideoStatsModule module = new VideoStatsModule();

            module.setComponent(delegate.controllerAction().frame().getFrame());
            module.setFixedSize(true);

            configureLink(module, sliderState);

            module.addModuleClosedListener(new EndThreadedImageStackProvider(producer));

            return module;
        };
    }

    private void configureLink(VideoStatsModule module, ISliderState sliderState) {
        LinkModules link = new LinkModules(module);

        if (indexesAreFrames) {
            sliderState.addIndexTo(link.getFrameIndex());
        }

        sliderState.addSliceTo(link.getSliceNum());
    }

    public ControllerPopupMenu controllerPopupMenu() {
        return delegate.controllerPopupMenu();
    }

    public InternalFrameCanvas getFrameCanvas() {
        return delegate.getFrameCanvas();
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public ControllerAction controllerAction() {
        return delegate.controllerAction();
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    public boolean addAdditionalDetails(IGenerateExtraDetail arg0) {
        return delegate.addAdditionalDetails(arg0);
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }

    public void flush() {
        delegate.flush();
    }

    public ImageDimensions getDimensions() {
        return delegate.getDimensions();
    }

    public IIndexGettableSettable getIndexGettableSettable() {
        return producer.getIndexGettableSettable();
    }
}
