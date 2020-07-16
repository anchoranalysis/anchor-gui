/* (C)2020 */
package org.anchoranalysis.gui.plot.creator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.gui.io.loader.manifest.finder.csvstatistic.CSVStatistic;
import org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder.FinderHistoryFolder;

public abstract class BridgedGraphFromDualFinderCreator<T>
        implements GraphFromDualFinderCreator<T> {

    @Override
    public BoundedIndexContainer<T> createCntr(FinderCSVStats finderCSVStats)
            throws CreateException {

        try {
            return new BoundedIndexContainerBridgeWithoutIndex<>(
                    finderCSVStats.get(), createCSVStatisticBridge());
        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public BoundedIndexContainer<T> createCntr(
            FinderHistoryFolder<CfgNRGInstantState> finderCfgNRGHistory) throws CreateException {

        try {
            return new BoundedIndexContainerBridgeWithoutIndex<>(
                    finderCfgNRGHistory.get().getCntr(), createCfgNRGInstantStateBridge());
        } catch (GetOperationFailedException e) {
            throw new CreateException(e);
        }
    }

    public abstract FunctionWithException<CSVStatistic, T, CreateException>
            createCSVStatisticBridge();

    public abstract FunctionWithException<CfgNRGInstantState, T, CreateException>
            createCfgNRGInstantStateBridge();
}
