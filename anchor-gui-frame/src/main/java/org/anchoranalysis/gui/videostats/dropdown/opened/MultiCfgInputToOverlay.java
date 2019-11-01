package org.anchoranalysis.gui.videostats.dropdown.opened;

import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.bridge.IObjectBridgeIndex;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

class MultiCfgInputToOverlay implements IObjectBridgeIndex<MultiInput<Cfg>, OverlayedInstantState> {
	
	private MarkDisplaySettings markDisplaySettings;
	
	public MultiCfgInputToOverlay(MarkDisplaySettings markDisplaySettings) {
		super();
		this.markDisplaySettings = markDisplaySettings;
	}

	@Override
	public OverlayedInstantState bridgeElement(int index,
			MultiInput<Cfg> sourceObject)
			throws GetOperationFailedException {
		try {
			Cfg cfg = sourceObject.getAssociatedObjects().doOperation();
			
			OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
				cfg,
				markDisplaySettings.regionMembership()
			);
			return new OverlayedInstantState(index, oc);
		} catch (ExecuteException e) {
			throw new GetOperationFailedException(e);
		}
	}
}