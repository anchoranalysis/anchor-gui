/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.frame.threaded.stack;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.GenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.InternalFrameWithDetailsTopPanel;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InitialSliderState;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.dimensions.Dimensions;

public class InternalFrameThreadedProvider {

    private InternalFrameWithDetailsTopPanel delegate;

    private boolean indexesAreFrames = false;

    private ThreadedProducer producer;

    public InternalFrameThreadedProvider(String title, boolean indexesAreFrames) {
        delegate = new InternalFrameWithDetailsTopPanel(title);
        this.indexesAreFrames = indexesAreFrames;
    }

    public SliderState init(
            ThreadedProducer producer,
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

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return defaultFrameState -> {
            VideoStatsModule module = new VideoStatsModule();

            module.setComponent(delegate.controllerAction().frame().getFrame());
            module.setFixedSize(true);

            configureLink(module, sliderState);

            module.addModuleClosedListener(new EndThreadedImageStackProvider(producer));

            return module;
        };
    }

    private void configureLink(VideoStatsModule module, SliderState sliderState) {
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

    public boolean addAdditionalDetails(GenerateExtraDetail arg0) {
        return delegate.addAdditionalDetails(arg0);
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }

    public void flush() {
        delegate.flush();
    }

    public Dimensions dimensions() {
        return delegate.dimensions();
    }

    public IndexGettableSettable getIndexGettableSettable() {
        return producer.getIndexGettableSettable();
    }
}
