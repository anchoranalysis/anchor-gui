/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder.historyfolder;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGNonHandleInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.AnchorNeverOccursException;

class CfgNRGInstantStateFromCfgNRGBridge
        implements BridgeElementWithIndex<CfgNRG, CfgNRGInstantState, AnchorNeverOccursException> {

    @Override
    public CfgNRGInstantState bridgeElement(int index, CfgNRG sourceObject) {
        return new CfgNRGNonHandleInstantState(index, sourceObject);
    }
}
