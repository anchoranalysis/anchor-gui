package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.energy.EnergyStack;

@FunctionalInterface
public interface EnergyStackSupplier {

    EnergyStack get() throws GetOperationFailedException;

    public static EnergyStackSupplier cache(EnergyStackSupplier supplier) {
        return CachedSupplier.cache(supplier::get)::get;
    }
}
