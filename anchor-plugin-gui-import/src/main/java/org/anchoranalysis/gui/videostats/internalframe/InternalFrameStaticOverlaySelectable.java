/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.videostats.internalframe;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.IndexableOverlays;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;

public class InternalFrameStaticOverlaySelectable {

    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public InternalFrameStaticOverlaySelectable(String title, boolean sendReceiveIndices) {
        this.delegate =
                new InternalFrameOverlayedInstantStateToRGBSelectable(
                        title, false, sendReceiveIndices);
    }

    public SliderState init(
            OverlayCollection oc, DefaultModuleState defaultState, VideoStatsModuleGlobalParams mpg)
            throws InitException {

        IndexableOverlays cis = new IndexableOverlays(0, oc);

        SingleContainer<IndexableOverlays> marksCntr = new SingleContainer<>(false);
        marksCntr.setItem(cis, cis.getIndex());

        IDGetter<Overlay> idGetter = new IDGetterOverlayID();

        SliderState sliderState =
                this.delegate.init(
                        marksCntr,
                        mpg.getDefaultColorIndexForMarks(),
                        idGetter,
                        idGetter,
                        false,
                        defaultState,
                        mpg);

        this.delegate.setIndexSliderVisible(false);

        return sliderState;
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu(SliderState sliderState) {
        return delegate.controllerBackgroundMenu(sliderState);
    }

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
