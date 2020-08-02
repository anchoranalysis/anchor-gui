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

package org.anchoranalysis.gui.frame.overlays;

import java.util.Optional;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.overlays.onrgb.InternalFrameEditableOverlays;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultStateSliderState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.stack.DisplayStack;

// A frame that supports quickly drawing marks on top of existing images based upon a mouse-click
//  at a particular point
public class InternalFrameOverlaysRedraw {

    private InternalFrameEditableOverlays delegate;

    private DisplayStack background;

    private PropertyValueChangeListenerList<OverlayCollection> eventListenerList =
            new PropertyValueChangeListenerList<>();

    public InternalFrameOverlaysRedraw(String title) {
        this.delegate = new InternalFrameEditableOverlays(title);
        this.delegate.controllerImageView().setEnforceMinimumSizeAfterGuessZoom(true);
    }

    public ISliderState init(DefaultModuleState defaultState, VideoStatsModuleGlobalParams mpg)
            throws InitException {

        ISliderState sliderState = delegate.init(defaultState, mpg);

        // For now we keep background as it is
        try {
            background = defaultState.getLinkState().getBackground().apply(0);
        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }

        return sliderState;
    }

    // Note due to synchronisation issues, should only be called once per frame
    public IShowOverlays showOverlays(ISliderState sliderState) {
        return (redrawUpdate) -> {
            synchronized (this) {
                delegate.applyUpdate(redrawUpdate.getUpdate());

                maybeChangeSlice(sliderState, redrawUpdate.getSuggestedSliceNum());

                triggerPropertyValueChanged(redrawUpdate.getOverlaysForTrigger());
            }
        };
    }

    public IModuleCreatorDefaultStateSliderState moduleCreator() {
        return (DefaultModuleState defaultFrameState, ISliderState sliderState) -> {
            VideoStatsModule module =
                    delegate.moduleCreator(sliderState).createVideoStatsModule(defaultFrameState);

            LinkModules link = new LinkModules(module);
            link.getOverlays().add(Optional.of(eventListenerList.createPropertyValueReceivable()));
            return module;
        };
    }

    public InternalFrameEditableOverlays getDelegate() {
        return delegate;
    }

    public DisplayStack getBackground() {
        return background;
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
        return delegate.controllerBackgroundMenu();
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }

    public ExtractOverlays extractOverlays() {
        return delegate.extractOverlays();
    }

    public ControllerAction controllerAction() {
        return delegate.controllerAction();
    }

    private static void maybeChangeSlice(ISliderState sliderState, int suggestedSliceNum) {
        if (suggestedSliceNum != -1) {
            sliderState.setSliceNum(suggestedSliceNum);
        }
    }

    private void triggerPropertyValueChanged(OverlayCollection oc) {

        for (PropertyValueChangeListener<OverlayCollection> l : eventListenerList) {
            l.propertyValueChanged(new PropertyValueChangeEvent<>(this, oc, false));
        }
    }
}
