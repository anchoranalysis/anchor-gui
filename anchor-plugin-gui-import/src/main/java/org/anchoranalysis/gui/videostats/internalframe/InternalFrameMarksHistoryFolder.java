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

import org.anchoranalysis.anchor.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.anchor.mpp.feature.instantstate.EnergyInstantStateBridge;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.id.IDGetterOverlayID;

public class InternalFrameMarksHistoryFolder {

    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public InternalFrameMarksHistoryFolder(String title) {
        this.delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(title, true, true);
    }

    public SliderState init(
            LoadContainer<IndexableMarksWithEnergy> history,
            DefaultModuleState defaultState,
            BackgroundSetProgressingSupplier backgroundSet,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        IDGetter<Overlay> idGetter = new IDGetterOverlayID();

        SliderState sliderState =
                this.delegate.init(
                        new BoundedIndexContainerBridgeWithoutIndex<>(
                                history.getContainer(),
                                new EnergyInstantStateBridge(
                                        defaultState.getMarkDisplaySettings().regionMembership())),
                        mpg.getDefaultColorIndexForMarks(),
                        idGetter,
                        idGetter,
                        !history.isExpensiveLoad(),
                        defaultState,
                        mpg);

        delegate.controllerBackgroundMenu(sliderState).add(mpg, backgroundSet);

        return sliderState;
    }

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
