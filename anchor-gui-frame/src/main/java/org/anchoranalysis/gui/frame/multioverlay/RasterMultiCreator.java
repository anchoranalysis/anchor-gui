/* (C)2020 */
package org.anchoranalysis.gui.frame.multioverlay;

import java.util.List;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.addoverlays.AdderAddOverlaysWithStack;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;

public class RasterMultiCreator<T> extends VideoStatsModuleCreator {

    private final List<MultiInput<T>> list;
    private final String frameName;
    private final VideoStatsModuleGlobalParams moduleParamsGlobal;
    private final BridgeElementWithIndex<
                    MultiInput<T>, OverlayedInstantState, OperationFailedException>
            bridge;

    public RasterMultiCreator(
            List<MultiInput<T>> list,
            String frameName,
            VideoStatsModuleGlobalParams moduleParamsGlobal,
            BridgeElementWithIndex<MultiInput<T>, OverlayedInstantState, OperationFailedException>
                    bridge) {
        super();
        this.list = list;
        this.frameName = frameName;
        this.moduleParamsGlobal = moduleParamsGlobal;
        this.bridge = bridge;
        assert (moduleParamsGlobal.getExportPopupParams() != null);
    }

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            InternalFrameMultiOverlay<T> internalFrame = new InternalFrameMultiOverlay<>(frameName);
            SliderNRGState state =
                    internalFrame.init(
                            list,
                            bridge,
                            adder.getSubgroup().getDefaultModuleState(),
                            moduleParamsGlobal);

            // We create a special adder
            adder =
                    new AdderAddOverlaysWithStack(
                            adder,
                            moduleParamsGlobal.getThreadPool(),
                            moduleParamsGlobal.getLogger().errorReporter());

            adder = state.addNrgStackToAdder(adder);

            ModuleAddUtilities.add(adder, internalFrame.moduleCreator(state.getSlider()));

        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }
}
