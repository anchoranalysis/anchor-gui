/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.exporttask;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;

public class ExportTaskMergedCfgNRGInstantState
        extends ExportTaskRasterGeneratorFromBoundedIndexContainer<CfgNRGInstantState> {

    public void init() {
        super.setBridge(
                new MergedContainerBridge(
                        () ->
                                RegionMapSingleton.instance()
                                        .membershipWithFlagsForIndex(
                                                GlobalRegionIdentifiers.SUBMARK_INSIDE)));
    }
}
