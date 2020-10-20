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

package org.anchoranalysis.gui.frame.multioverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.core.index.BridgeElementWithIndex;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainerFromList;
import org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithIndex;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ImageStackContainerFromName;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.EnergyBackground;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.MultiInput;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.overlay.IndexableOverlays;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.identifier.IdentifierFromOverlay;

class InternalFrameMultiOverlay<T> {
    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public InternalFrameMultiOverlay(String frameName) {
        delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(frameName, false, true);
    }

    public SliderEnergyState init(
            final List<MultiInput<T>> list,
            BridgeElementWithIndex<MultiInput<T>, IndexableOverlays, OperationFailedException>
                    bridge,
            DefaultModuleStateManager defaultState,
            final VideoStatsModuleGlobalParams mpg)
            throws InitException {

        ImageStackContainerFromName imageStackCntrFromName = createImageStackCntr(list);

        // We assume all EnergyBackgrounds have the same stack-names, so it doesn't
        //  matter which is picked
        String arbitraryStackName =
                list.get(0).getEnergyBackground().arbitraryBackgroundStackName();
        DefaultModuleState defaultStateNew =
                assignInitialBackground(defaultState, arbitraryStackName, imageStackCntrFromName);

        IdentifierGetter<Overlay> idGetter = new IdentifierFromOverlay();

        SliderState sliderState =
                delegate.init(
                        bridgeList(list, bridge),
                        mpg.getDefaultColorIndexForMarks(),
                        idGetter,
                        idGetter,
                        false,
                        defaultStateNew,
                        mpg);

        addExtraDetail(list);
        addBackgroundMenu(list, sliderState, imageStackCntrFromName, mpg);

        EnergyBackground energyBackground = list.get(sliderState.getIndex()).getEnergyBackground();

        return new SliderEnergyState(sliderState, energyBackground);
    }

    private static <T> ImageStackContainerFromName createImageStackCntr(
            final List<MultiInput<T>> list) {
        return name ->
                sourceObject -> {
                    return list.get(sourceObject)
                            .getEnergyBackground()
                            .getBackgroundSet()
                            .get(ProgressReporterNull.get())
                            .singleStack(name);
                };
    }

    private static <T> BoundedIndexContainer<IndexableOverlays> bridgeList(
            List<T> list,
            BridgeElementWithIndex<T, IndexableOverlays, OperationFailedException> bridge) {
        BoundedIndexContainerFromList<T> cntr = new BoundedIndexContainerFromList<>(list);
        return new BoundedIndexContainerBridgeWithIndex<>(cntr, bridge);
    }

    private static DefaultModuleState assignInitialBackground(
            DefaultModuleStateManager defaultState,
            String stackName,
            ImageStackContainerFromName imageStackCntrFromName)
            throws InitException {

        // We always set an initial background
        try {
            return defaultState.copyChangeBackground(imageStackCntrFromName.get(stackName));

        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }

    private void addBackgroundMenu(
            List<MultiInput<T>> list,
            SliderState sliderState,
            ImageStackContainerFromName imageStackCntrFromName,
            VideoStatsModuleGlobalParams mpg) {
        ControllerPopupMenuWithBackground controller =
                delegate.controllerBackgroundMenu(sliderState);
        controller.add(
                namesFromCurrentBackground(list, sliderState, mpg.getLogger().errorReporter()),
                imageStackCntrFromName,
                mpg);
    }

    private IGetNames namesFromCurrentBackground(
            List<MultiInput<T>> list, SliderState sliderState, ErrorReporter errorReporter) {
        return () -> {
            try {
                Set<String> names =
                        list.get(sliderState.getIndex())
                                .getEnergyBackground()
                                .getBackgroundSet()
                                .get(ProgressReporterNull.get())
                                .names();
                return new ArrayList<>(names);

            } catch (BackgroundStackContainerException e) {
                errorReporter.recordError(InternalFrameMultiOverlay.class, e);
                return new ArrayList<>();
            }
        };
    }

    private void addExtraDetail(List<MultiInput<T>> list) {

        delegate.addAdditionalDetails(index -> String.format("id=%s", list.get(index).getName()));
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
