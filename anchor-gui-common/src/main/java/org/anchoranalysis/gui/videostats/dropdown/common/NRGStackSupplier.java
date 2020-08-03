package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@FunctionalInterface
public interface NRGStackSupplier {

    NRGStackWithParams get() throws GetOperationFailedException;
    
    public static NRGStackSupplier cache( NRGStackSupplier supplier ) {
        return CachedSupplier.cache( supplier::get )::get;
    }
}

