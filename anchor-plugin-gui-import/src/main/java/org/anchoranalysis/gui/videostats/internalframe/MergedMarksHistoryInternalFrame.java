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

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.overlay.IndexableOverlays;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.mergebridge.DualStateContainer;
import org.anchoranalysis.gui.mergebridge.MergeMarksBridge;
import org.anchoranalysis.gui.mergebridge.MergedColorIndex;
import org.anchoranalysis.gui.mergebridge.TransformToCfg;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;

public class MergedMarksHistoryInternalFrame {

    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public MergedMarksHistoryInternalFrame(String title) {
        this.delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(title, true, true);
    }

    public ISliderState init(
            LoadContainer<IndexableMarksWithEnergy> selectedHistory,
            LoadContainer<IndexableMarksWithEnergy> proposalHistory,
            DefaultModuleState defaultState,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // A container that supplies DualCfgInstantState
        DualStateContainer<MarkCollection> dualHistory =
                new DualStateContainer<>(
                        createInputList(selectedHistory, proposalHistory), new TransformToCfg());

        dualHistory.init();

        MergeMarksBridge mergeCfgBridge =
                new MergeMarksBridge(() -> defaultState.getMarkDisplaySettings().regionMembership());

        // We map each DualCfgInstantState
        BoundedIndexContainer<IndexableOverlays> cfgCntnr =
                new BoundedIndexContainerBridgeWithoutIndex<>(dualHistory, mergeCfgBridge);

        boolean eitherExpensiveLoad =
                selectedHistory.isExpensiveLoad() || proposalHistory.isExpensiveLoad();

        ColorIndex mergedColorIndex = new MergedColorIndex(mergeCfgBridge);

        return this.delegate.init(
                cfgCntnr,
                mergedColorIndex,
                new IDGetterOverlayID(),
                new IDGetterIter<>(),
                !eitherExpensiveLoad,
                defaultState,
                mpg);
    }

    public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
        return delegate.moduleCreator(sliderState);
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu(ISliderState sliderState) {
        return delegate.controllerBackgroundMenu(sliderState);
    }

    private static List<BoundedIndexContainer<IndexableMarksWithEnergy>> createInputList(
            LoadContainer<IndexableMarksWithEnergy> selectedHistory,
            LoadContainer<IndexableMarksWithEnergy> proposalHistory) {
        List<BoundedIndexContainer<IndexableMarksWithEnergy>> out = new ArrayList<>();
        out.add(selectedHistory.getContainer());
        out.add(proposalHistory.getContainer());
        return out;
    }
}