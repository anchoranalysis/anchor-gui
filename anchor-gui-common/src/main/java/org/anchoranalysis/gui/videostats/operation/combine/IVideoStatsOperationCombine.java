/* (C)2020 */
package org.anchoranalysis.gui.videostats.operation.combine;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.image.object.ObjectCollection;

//
public interface IVideoStatsOperationCombine {

    NRGBackground getNrgBackground();

    Optional<Operation<Cfg, OperationFailedException>> getCfg();

    Optional<Operation<ObjectCollection, OperationFailedException>> getObjects();

    String generateName();
}
