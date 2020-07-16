/* (C)2020 */
package org.anchoranalysis.gui.videostats;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

// Gets a raster associated with this module for calculating marks
@FunctionalInterface
public interface INRGStackGetter {

    NRGStackWithParams getAssociatedNrgStack() throws OperationFailedException;
}
