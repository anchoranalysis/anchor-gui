/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.opened;

import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjectFactory;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;
import org.anchoranalysis.image.object.ObjectCollection;

class MultiObjectsInputToOverlay
        implements BridgeElementWithIndex<
                MultiInput<ObjectCollection>, OverlayedInstantState, OperationFailedException> {

    @Override
    public OverlayedInstantState bridgeElement(int index, MultiInput<ObjectCollection> sourceObject)
            throws OperationFailedException {

        OverlayCollection oc =
                OverlayCollectionObjectFactory.createWithoutColor(
                        sourceObject.getAssociatedObjects().doOperation(), new IDGetterIter<>());
        return new OverlayedInstantState(index, oc);
    }
}
