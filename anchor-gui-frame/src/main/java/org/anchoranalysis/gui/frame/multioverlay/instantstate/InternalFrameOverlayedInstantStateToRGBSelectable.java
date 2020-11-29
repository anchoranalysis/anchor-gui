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

import java.util.Optional;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.identifier.getter.IdentifierGetter;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;
import org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.PropertyValueReceivableFromIndicesSelection;
import org.anchoranalysis.gui.frame.details.GenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.display.overlay.GetOverlayCollection;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.indices.DualIndicesSelection;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsList;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsOverlayCollection;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.IndexableColoredOverlays;
import org.anchoranalysis.gui.videostats.internalframe.markstorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.overlay.IndexableOverlays;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// An internal frame, that converts a configuration to RGB
public class InternalFrameOverlayedInstantStateToRGBSelectable {

    static Log log = LogFactory.getLog(InternalFrameOverlayedInstantStateToRGBSelectable.class);

    // The current selection within the frame
    private DualIndicesSelection selectionIndices = new DualIndicesSelection();
    private ClickAdapter markClickAdapter;

    private InternalFrameOverlayedInstantStateToRGB delegate;

    private CurrentlySelectedMarks currentlySelectedMarksGetter = new CurrentlySelectedMarks();

    private boolean sendReceiveIndices = false;

    // Returns a Marks representing the currently selected marks
    private class CurrentlySelectedMarks implements GetOverlayCollection {

        @Override
        public ColoredOverlayCollection getOverlays() {
            return delegate.getOverlayRetriever()
                    .getOverlays()
                    .createSubsetFromIDs(selectionIndices.getCurrentSelection()::contains);
        }
    }

    public InternalFrameOverlayedInstantStateToRGBSelectable(
            String title, boolean indexesAreFrames, boolean sendReceiveIndices) {
        delegate = new InternalFrameOverlayedInstantStateToRGB(title, indexesAreFrames);
        this.sendReceiveIndices = sendReceiveIndices;
    }

    // Must be called before usage
    public SliderState init(
            BoundedIndexContainer<IndexableOverlays> marksCntr,
            ColorIndex colorIndex,
            IdentifierGetter<Overlay> idGetter,
            IdentifierGetter<Overlay> idColorGetter,
            boolean includeFrameAdjusting,
            DefaultModuleState initialState,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // WE MUST SET THIS TO THE CORRECT initial state, as the frameIJ will not trigger events on
        // its default state, to correct itself
        this.selectionIndices.setCurrentSelection(initialState.getLinkState().getObjectIDs());

        // We create a wrapper that conditions the MarkDisplaySettings on the current selection
        MarkDisplaySettingsWrapper markDisplaySettingsWrapper =
                new MarkDisplaySettingsWrapper(
                        initialState.getMarkDisplaySettings().duplicate(),
                        (ObjectWithProperties object, RGBStack stack, int id) ->
                                selectionIndices.getCurrentSelection().contains(id));

        BoundedIndexContainer<IndexableColoredOverlays> marksCntrColored =
                new BoundedIndexContainerBridgeWithoutIndex<>(
                        marksCntr, new AddColorBridge(colorIndex, idColorGetter));

        SliderState sliderState =
                delegate.init(
                        marksCntrColored,
                        idGetter,
                        includeFrameAdjusting,
                        initialState,
                        markDisplaySettingsWrapper,
                        createElementRetriever(),
                        mpg);

        GenerateExtraDetail marksSizeDetail =
                index -> {
                    OverlayRetriever or = delegate.getOverlayRetriever();
                    return String.format(
                            "marksSize=%s",
                            or.getOverlays() != null ? or.getOverlays().size() : -1);
                };

        delegate.addAdditionalDetails(marksSizeDetail);

        // When a new object is selected, then we need to redraw (partially)
        new PropertyValueReceivableFromIndicesSelection(selectionIndices.getCurrentSelection())
                .addPropertyValueChangeListener(
                        new RedrawFromMarksGetter(
                                currentlySelectedMarksGetter, delegate.getRedrawable()));

        markClickAdapter =
                new ClickAdapter(selectionIndices, sliderState, delegate.getOverlayRetriever());
        delegate.controllerAction().mouse().addMouseListener(markClickAdapter, false);

        return sliderState;
    }

    private IRetrieveElements createElementRetriever() {
        return () -> {
            RetrieveElementsList rel = new RetrieveElementsList();
            rel.add(delegate.getElementRetriever().retrieveElements());

            RetrieveElementsOverlayCollection rempp = new RetrieveElementsOverlayCollection();
            rempp.setCurrentSelectedObjects(
                    currentlySelectedMarksGetter.getOverlays().getOverlays());
            rempp.setCurrentObjects(delegate.getOverlayRetriever().getOverlays().getOverlays());

            rel.add(rempp);
            return rel;
        };
    }

    private void addSendReceiveIndicesToModule(VideoStatsModule module) {

        LinkModules link = new LinkModules(module);
        link.getMarkIndices()
                .add(
                        Optional.of(
                                new PropertyValueReceivableFromIndicesSelection(
                                        selectionIndices.getLastExplicitSelection())),
                        Optional.of(
                                (value, adjusting) -> {
                                    // If the ids are the same as our current selection, we don't
                                    // need to change
                                    // anything
                                    if (!selectionIndices.setCurrentSelection(value.getArr())) {
                                        return;
                                    }
                                }));
    }

    public IModuleCreatorDefaultState moduleCreator(SliderState sliderState) {
        return defaultFrameState -> {
            VideoStatsModule module =
                    delegate.moduleCreator(sliderState).createVideoStatsModule(defaultFrameState);

            if (sendReceiveIndices) {
                addSendReceiveIndicesToModule(module);
            }

            LinkModules link = new LinkModules(module);
            link.getOverlays()
                    .add(Optional.of(markClickAdapter.createSelectOverlayCollectionReceivable()));
            return module;
        };
    }

    public void setIndexSliderVisible(boolean visibility) {
        delegate.setIndexSliderVisible(visibility);
    }

    public void flush() {
        delegate.flush();
    }

    public IPropertyValueReceivable<OverlayCollection> createSelectMarksReceivable() {
        return markClickAdapter.createSelectOverlayCollectionReceivable();
    }

    public InternalFrameCanvas getFrameCanvas() {
        return delegate.getFrameCanvas();
    }

    public IRetrieveElements getElementRetriever() {
        return delegate.getElementRetriever();
    }

    public boolean addAdditionalDetails(GenerateExtraDetail arg0) {
        return delegate.addAdditionalDetails(arg0);
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu(SliderState sliderState) {
        return delegate.controllerBackgroundMenu(sliderState);
    }
}
