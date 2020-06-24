package org.anchoranalysis.gui.videostats.operation.combine;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.image.objectmask.ObjectCollection;

// 
/**
 * Combines operations, shouldn't be dependent on the specific data of any other operation
 * 
 * @author Owen Feehan
 *
 */
public interface IVideoStatsOperationCombine {
	
	NRGBackground getNrgBackground();
	
	Operation<Cfg,OperationFailedException> getCfg();
	
	Operation<ObjectCollection,OperationFailedException> getObjMaskCollection();
	
	String generateName();
}
