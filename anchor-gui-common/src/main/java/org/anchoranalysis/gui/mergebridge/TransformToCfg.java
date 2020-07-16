/* (C)2020 */
package org.anchoranalysis.gui.mergebridge;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.gui.mergebridge.DualCfgNRGContainer.TransformInstanteState;

public class TransformToCfg implements TransformInstanteState<Cfg> {

    @Override
    public Cfg transform(CfgNRGInstantState state) {
        if (state != null && state.getCfgNRG() != null) {
            return state.getCfgNRG().getCfg();
        } else {
            return null;
        }
    }
}
