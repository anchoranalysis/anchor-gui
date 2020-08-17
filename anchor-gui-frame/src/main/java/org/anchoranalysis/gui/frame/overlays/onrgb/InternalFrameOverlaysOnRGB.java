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

package org.anchoranalysis.gui.frame.overlays.onrgb;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.IndexGettableSettable;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.details.GenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.overlays.ExtractOverlays;
import org.anchoranalysis.gui.frame.threaded.overlay.InternalFrameThreadedOverlayProvider;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class InternalFrameOverlaysOnRGB {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(InternalFrameOverlaysOnRGB.class);

    private InternalFrameThreadedOverlayProvider delegate;

    private SingleContainer<OverlayedDisplayStack> cntr;
    private ControllerPopupMenuWithBackground controllerMenu;
    private ExtractOverlays extractOverlays;

    public InternalFrameOverlaysOnRGB(String title, boolean indexesAreFrames) {
        delegate = new InternalFrameThreadedOverlayProvider(title, indexesAreFrames);

        cntr = new SingleContainer<>(false);
    }

    // Must be called before usage
    public ISliderState init(
            OverlayedDisplayStack overlayedDisplayStack,
            IDGetter<Overlay> idGetter,
            boolean includeFrameAdjusting,
            DefaultModuleState initialState,
            MarkDisplaySettingsWrapper markDisplaySettingsWrapper,
            IRetrieveElements elementRetriever,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        assert (initialState.getLinkState().getBackground() != null);

        cntr.setItem(overlayedDisplayStack, 0);

        CheckedFunction<Integer, OverlayedDisplayStack, BackgroundStackContainerException>
                bridge = new CfgCntrBridge(cntr);

        delegate.beforeInit(bridge, idGetter, 0, markDisplaySettingsWrapper, mpg);

        BackgroundSetterLocal backgroundSetter =
                new BackgroundSetterLocal(delegate.getRedrawable());
        extractOverlays = new OverlayerExtracter();

        // We assume all channels have the same number of slices
        ISliderState sliderState =
                delegate.init(cntr, includeFrameAdjusting, initialState, elementRetriever, mpg);

        // This must be done after init() on delegate, otherwise controllerPopupMenu() is null
        controllerMenu =
                new ControllerPopupMenuWithBackground(
                        delegate.controllerPopupMenu(), backgroundSetter);

        return sliderState;
    }

    private class OverlayerExtracter implements ExtractOverlays {

        @Override
        public ColoredOverlayCollection getOverlays() {
            return delegate.getOverlayRetriever().getOverlays();
        }

        @Override
        public ImageDimensions dimensions() {
            return delegate.dimensions();
        }
    }

    public InternalFrameCanvas getFrameCanvas() {
        return delegate.getFrameCanvas();
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public void flush() {
        delegate.flush();
    }

    public IRedrawable getRedrawable() {
        return delegate.getRedrawable();
    }

    public boolean addAdditionalDetails(GenerateExtraDetail arg0) {
        return delegate.addAdditionalDetails(arg0);
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
        return controllerMenu;
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }

    public ExtractOverlays extractOverlays() {
        return extractOverlays;
    }

    public ControllerAction controllerAction() {
        return delegate.controllerAction();
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }

    public IndexGettableSettable getIndexGettableSettable() {
        return delegate.getIndexGettableSettable();
    }
}
