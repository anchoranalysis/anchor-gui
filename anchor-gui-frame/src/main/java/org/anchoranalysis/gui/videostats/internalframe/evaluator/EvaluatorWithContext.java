/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import lombok.Value;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@Value
public class EvaluatorWithContext {
    ProposalOperationCreator evaluator;
    NRGStackWithParams nrgStack;
    CfgGen cfgGen;
    RegionMap regionMap;
}
