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
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.BoundedIndexContainerFromList;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithIndex;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ImageStackContainerFromName;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;

class InternalFrameMultiOverlay<T> {
    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public InternalFrameMultiOverlay(String frameName) {
        delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(frameName, false, true);
    }

    public SliderNRGState init(
            final List<MultiInput<T>> list,
            BridgeElementWithIndex<MultiInput<T>, OverlayedInstantState, OperationFailedException>
                    bridge,
            DefaultModuleStateManager defaultState,
            final VideoStatsModuleGlobalParams mpg)
            throws InitException {

        ImageStackContainerFromName imageStackCntrFromName = createImageStackCntr(list);

        // We assume all NRGBackgrounds have the same stack-names, so it doesn't
        //  matter which is picked
        String arbitraryStackName = list.get(0).getNrgBackground().arbitraryBackgroundStackName();
        DefaultModuleState defaultStateNew =
                assignInitialBackground(defaultState, arbitraryStackName, imageStackCntrFromName);

        IDGetter<Overlay> idGetter = new IDGetterOverlayID();

        ISliderState sliderState =
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

        NRGBackground nrgBackground = list.get(sliderState.getIndex()).getNrgBackground();

        return new SliderNRGState(sliderState, nrgBackground);
    }

    private static <T> ImageStackContainerFromName createImageStackCntr(
            final List<MultiInput<T>> list) {
        return name ->
                sourceObject -> {
                    return list.get(sourceObject)
                            .getNrgBackground()
                            .getBackgroundSet()
                            .doOperation(ProgressReporterNull.get())
                            .singleStack(name);
                };
    }

    private static <T> BoundedIndexContainer<OverlayedInstantState> bridgeList(
            List<T> list,
            BridgeElementWithIndex<T, OverlayedInstantState, OperationFailedException> bridge) {
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
            return defaultState.copyChangeBackground(
                    imageStackCntrFromName.imageStackCntrFromName(stackName));

        } catch (BackgroundStackContainerException e) {
            throw new InitException(e);
        }
    }

    private void addBackgroundMenu(
            List<MultiInput<T>> list,
            ISliderState sliderState,
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
            List<MultiInput<T>> list, ISliderState sliderState, ErrorReporter errorReporter) {
        return () -> {
            try {
                Set<String> names =
                        list.get(sliderState.getIndex())
                                .getNrgBackground()
                                .getBackgroundSet()
                                .doOperation(ProgressReporterNull.get())
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

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }
}
