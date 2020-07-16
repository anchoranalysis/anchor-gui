/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.opened;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;

class MultiCfgInputToOverlay
        implements BridgeElementWithIndex<
                MultiInput<Cfg>, OverlayedInstantState, OperationFailedException> {

    private MarkDisplaySettings markDisplaySettings;

    public MultiCfgInputToOverlay(MarkDisplaySettings markDisplaySettings) {
        super();
        this.markDisplaySettings = markDisplaySettings;
    }

    @Override
    public OverlayedInstantState bridgeElement(int index, MultiInput<Cfg> sourceObject)
            throws OperationFailedException {

        Cfg cfg = sourceObject.getAssociatedObjects().doOperation();

        OverlayCollection oc =
                OverlayCollectionMarkFactory.createWithoutColor(
                        cfg, markDisplaySettings.regionMembership());
        return new OverlayedInstantState(index, oc);
    }
}
