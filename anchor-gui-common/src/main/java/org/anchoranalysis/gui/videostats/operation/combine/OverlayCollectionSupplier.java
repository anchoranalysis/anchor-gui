package org.anchoranalysis.gui.videostats.operation.combine;

import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Supplies some kind of overlay-collection
 * 
 * @author Owen Feehan
 *
 * @param <T> overlay-collection type
 */
@FunctionalInterface
public interface OverlayCollectionSupplier<T> {

    T get() throws OperationFailedException;
    
    public static <T> OverlayCollectionSupplier<T> cache(OverlayCollectionSupplier<T> supplier) {
        return CachedSupplier.cache(supplier::get)::get;
    }
}
