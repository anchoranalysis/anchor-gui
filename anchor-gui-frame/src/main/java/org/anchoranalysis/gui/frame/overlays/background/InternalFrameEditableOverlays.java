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

package org.anchoranalysis.gui.frame.overlays.background;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.overlays.ExtractOverlays;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsList;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsOverlayCollection;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.ColoredMarksUpdater;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.id.IDGetterOverlayID;

public class InternalFrameEditableOverlays implements ColoredMarksUpdater {

    private InternalFrameOverlaysOnRGB delegate;

    public InternalFrameEditableOverlays(String title) {
        this.delegate = new InternalFrameOverlaysOnRGB(title, false);
    }

    public SliderState init(DefaultModuleState defaultState, VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // We define our mark display settings without making them conditional on anything
        MarkDisplaySettingsWrapper markDisplaySettings =
                new MarkDisplaySettingsWrapper(defaultState.getMarkDisplaySettings().duplicate());

        try {
            SliderState sliderState =
                    this.delegate.init(
                            new OverlayedDisplayStack(
                                    new ColoredOverlayCollection(),
                                    defaultState.getLinkState().getBackground().apply(0)),
                            new IDGetterOverlayID(),
                            false,
                            defaultState,
                            markDisplaySettings,
                            createElementRetriever(),
                            mpg);
            this.delegate.setIndexSliderVisible(false);
            return sliderState;

        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }

    // We send an update to the Marks
    @Override
    public synchronized void applyUpdate(OverlayedDisplayStackUpdate update) {
        this.delegate.getRedrawable().applyRedrawUpdate(update);
    }

    private static class RetrieveElementsLocal implements IRetrieveElements {

        private InternalFrameOverlaysOnRGB internalFrame;

        public RetrieveElementsLocal(InternalFrameOverlaysOnRGB internalFrame) {
            super();
            this.internalFrame = internalFrame;
        }

        @Override
        public RetrieveElements retrieveElements() {

            RetrieveElementsList rel = new RetrieveElementsList();
            rel.add(internalFrame.getElementRetriever().retrieveElements());

            RetrieveElementsOverlayCollection rempp = new RetrieveElementsOverlayCollection();
            rempp.setCurrentObjects(internalFrame.extractOverlays().getOverlays().getOverlays());

            rel.add(rempp);
            return rel;
        }
    }

    private IRetrieveElements createElementRetriever() {
        return new RetrieveElementsLocal(delegate);
    }

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return delegate.moduleCreator(sliderState);
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
}
