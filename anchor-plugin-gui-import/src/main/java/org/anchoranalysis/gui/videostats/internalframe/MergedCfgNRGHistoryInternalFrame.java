/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.mergebridge.DualCfgNRGContainer;
import org.anchoranalysis.gui.mergebridge.MergeCfgBridge;
import org.anchoranalysis.gui.mergebridge.MergedColorIndex;
import org.anchoranalysis.gui.mergebridge.TransformToCfg;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;

public class MergedCfgNRGHistoryInternalFrame {

    private InternalFrameOverlayedInstantStateToRGBSelectable delegate;

    public MergedCfgNRGHistoryInternalFrame(String title) {
        this.delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(title, true, true);
    }

    public ISliderState init(
            LoadContainer<CfgNRGInstantState> selectedHistory,
            LoadContainer<CfgNRGInstantState> proposalHistory,
            DefaultModuleState defaultState,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        // A container that supplies DualCfgInstantState
        DualCfgNRGContainer<Cfg> dualHistory =
                new DualCfgNRGContainer<>(
                        createInputList(selectedHistory, proposalHistory), new TransformToCfg());

        dualHistory.init();

        MergeCfgBridge mergeCfgBridge =
                new MergeCfgBridge(() -> defaultState.getMarkDisplaySettings().regionMembership());

        // We map each DualCfgInstantState
        BoundedIndexContainer<OverlayedInstantState> cfgCntnr =
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

    private static List<BoundedIndexContainer<CfgNRGInstantState>> createInputList(
            LoadContainer<CfgNRGInstantState> selectedHistory,
            LoadContainer<CfgNRGInstantState> proposalHistory) {
        List<BoundedIndexContainer<CfgNRGInstantState>> out = new ArrayList<>();
        out.add(selectedHistory.getCntr());
        out.add(proposalHistory.getCntr());
        return out;
    }
}
