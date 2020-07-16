/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGNonHandleInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.ContainerUtilities;
import org.anchoranalysis.gui.mergebridge.DualCfgNRGContainer;
import org.anchoranalysis.gui.mergebridge.MergeCfgBridge;
import org.anchoranalysis.gui.mergebridge.MergedColorIndex;
import org.anchoranalysis.gui.mergebridge.TransformToCfg;

@RequiredArgsConstructor
class MergedContainerBridge
        implements FunctionWithException<
                ExportTaskParams,
                BoundedIndexContainer<CfgNRGInstantState>,
                OperationFailedException> {

    // START REQUIRED ARGUMENTS
    private final Supplier<RegionMembershipWithFlags> regionMembership;
    // END REQUIRED ARGUMENTS

    private BoundedIndexContainerBridgeWithoutIndex<
                    OverlayedInstantState, CfgNRGInstantState, AnchorImpossibleSituationException>
            retBridge = null;

    @Override
    public BoundedIndexContainer<CfgNRGInstantState> apply(ExportTaskParams sourceObject)
            throws OperationFailedException {

        // TODO fix
        if (retBridge == null) {

            DualCfgNRGContainer<Cfg> dualHistory;

            try {
                dualHistory =
                        new DualCfgNRGContainer<>(
                                ContainerUtilities.listCntrs(
                                        sourceObject.getAllFinderCfgNRGHistory()),
                                new TransformToCfg());

                dualHistory.init();
            } catch (GetOperationFailedException e) {
                throw new OperationFailedException(e);
            }

            MergeCfgBridge mergeCfgBridge = new MergeCfgBridge(regionMembership);

            BoundedIndexContainer<OverlayedInstantState> cfgCntr =
                    new BoundedIndexContainerBridgeWithoutIndex<>(dualHistory, mergeCfgBridge);

            // TODO HACK to allow exportparams to work
            sourceObject.setColorIndexMarks(new MergedColorIndex(mergeCfgBridge));

            retBridge =
                    new BoundedIndexContainerBridgeWithoutIndex<>(
                            cfgCntr,
                            s -> {
                                Cfg cfg =
                                        OverlayCollectionMarkFactory.cfgFromOverlays(
                                                s.getOverlayCollection());
                                return new CfgNRGNonHandleInstantState(
                                        s.getIndex(),
                                        new CfgNRG(
                                                new CfgWithNRGTotal(
                                                        cfg,
                                                        null)) // TODO This null seems wrong, fix!
                                        );
                            });
        }
        return retBridge;
    }
}
