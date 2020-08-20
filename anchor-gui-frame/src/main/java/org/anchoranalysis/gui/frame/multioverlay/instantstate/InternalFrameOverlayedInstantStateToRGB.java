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

package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.details.GenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.frame.threaded.overlay.InternalFrameThreadedOverlayProvider;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundSetter;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.stack.DisplayStack;

class InternalFrameOverlayedInstantStateToRGB {

    private InternalFrameThreadedOverlayProvider delegate;

    private IndexToRedrawUpdate indexToRedrawUpdate;

    public InternalFrameOverlayedInstantStateToRGB(String title, boolean indexesAreFrames) {
        delegate = new InternalFrameThreadedOverlayProvider(title, indexesAreFrames);
    }

    // Must be called before usage
    public ISliderState init(
            BoundedIndexContainer<ColoredOverlayedInstantState> overlaysCntr,
            IDGetter<Overlay> idGetter,
            boolean includeFrameAdjusting,
            DefaultModuleState initialState,
            MarkDisplaySettingsWrapper markDisplaySettingsWrapper,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        assert (initialState.getLinkState().getBackground() != null);

        indexToRedrawUpdate =
                new IndexToRedrawUpdate(overlaysCntr, initialState.getLinkState().getBackground());

        delegate.beforeInit(
                indexToRedrawUpdate,
                idGetter,
                overlaysCntr.previousEqualIndex(delegate.defaultIndex(initialState)),
                markDisplaySettingsWrapper,
                mpg);

        // We assume all channels have the same number of slices
        return delegate.init(
                overlaysCntr, includeFrameAdjusting, initialState, elementRetriever, mpg);
    }

    public InternalFrameCanvas getFrameCanvas() {
        return delegate.getFrameCanvas();
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return defaultFrameState -> {
            VideoStatsModule module =
                    delegate.moduleCreator(sliderState).createVideoStatsModule(defaultFrameState);

            LinkModules link = new LinkModules(module);
            link.getBackground()
                    .add(new BackgroundSendable(indexToRedrawUpdate, delegate.getRedrawable()));

            return module;
        };
    }

    @AllArgsConstructor
    private static class BackgroundSendable
            implements IPropertyValueSendable<
                    CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException>> {

        private IndexToRedrawUpdate indexToRedrawUpdate;
        private IRedrawable redrawable;

        @Override
        public void setPropertyValue(
                CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException> value,
                boolean adjusting) {
            indexToRedrawUpdate.setImageStackCntr(value);
            redrawable.applyRedrawUpdate(OverlayedDisplayStackUpdate.redrawAll());
        }
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public OverlayRetriever getOverlayRetriever() {
        return delegate.getOverlayRetriever();
    }

    public void flush() {
        delegate.flush();
    }

    public Dimensions dimensions() {
        return delegate.dimensions();
    }

    public IndexGettableSettable getIndexGettableSettable() {
        return delegate.getIndexGettableSettable();
    }

    public boolean addAdditionalDetails(GenerateExtraDetail arg0) {
        return delegate.addAdditionalDetails(arg0);
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu(ISliderState sliderState) {
        return new ControllerPopupMenuWithBackground(
                delegate.controllerPopupMenu(), createBackgroundSetter(sliderState));
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }

    public ControllerAction controllerAction() {
        return delegate.controllerAction();
    }

    public IRedrawable getRedrawable() {
        return delegate.getRedrawable();
    }

    private IBackgroundSetter createBackgroundSetter(ISliderState sliderState) {
        return imageStackCntr -> {
            indexToRedrawUpdate.setImageStackCntr(imageStackCntr);

            try {
                DisplayStack backgroundNew = imageStackCntr.apply(sliderState.getIndex());
                delegate.getRedrawable()
                        .applyRedrawUpdate(
                                OverlayedDisplayStackUpdate.assignBackground(backgroundNew));
            } catch (BackgroundStackContainerException e) {
                throw new SetOperationFailedException(e);
            }
        };
    }
}
