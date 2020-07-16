/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import java.util.List;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.container.ContainerUtilities;
import org.anchoranalysis.gui.mergebridge.DualCfgNRGContainer;

public class ExportTaskCfgNRGInstantState
        extends ExportTaskRasterGeneratorFromBoundedIndexContainer<
                DualStateWithoutIndex<CfgNRGInstantState>> {

    @Override
    public void init() {
        setBridge(this::convert);
    }

    private BoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> createPrimaryOnly(
            ExportTaskParams sourceObject) throws GetOperationFailedException {
        return new BoundedIndexContainerBridgeWithoutIndex<>(
                sourceObject.getFinderCfgNRGHistory().getCntr(), DualStateWithoutIndex::new);
    }

    private static DualCfgNRGContainer<CfgNRGInstantState> combine(
            List<ContainerGetter<CfgNRGInstantState>> cntrs) throws GetOperationFailedException {

        DualCfgNRGContainer<CfgNRGInstantState> dualHistory =
                new DualCfgNRGContainer<>(ContainerUtilities.listCntrs(cntrs), a -> a);

        dualHistory.init();
        return dualHistory;
    }

    private DualCfgNRGContainer<CfgNRGInstantState> mergedHistory(ExportTaskParams sourceObject)
            throws GetOperationFailedException {
        return combine(sourceObject.getAllFinderCfgNRGHistory());
    }

    private BoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> createMergedBridge(
            ExportTaskParams sourceObject) throws GetOperationFailedException {

        // Otherwise we merge

        DualCfgNRGContainer<CfgNRGInstantState> dualHistory = mergedHistory(sourceObject);
        dualHistory.init();

        return new BoundedIndexContainerBridgeWithoutIndex<>(
                dualHistory, s -> new DualStateWithoutIndex<>(s.getList()));
    }

    private BoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> convert(
            ExportTaskParams sourceObject) throws OperationFailedException {
        assert (sourceObject.numCfgNRGHistory() > 0);

        try {
            if (sourceObject.numCfgNRGHistory() == 1) {
                return createPrimaryOnly(sourceObject);
            } else {
                return createMergedBridge(sourceObject);
            }
        } catch (GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }
}
