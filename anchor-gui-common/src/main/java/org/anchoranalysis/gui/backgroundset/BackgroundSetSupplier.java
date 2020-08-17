package org.anchoranalysis.gui.backgroundset;

import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;

/**
 * Supplier of an object for a store
 *
 * @author Owen Feehan
 * @param <T> type supplied to the store
 */
@FunctionalInterface
public interface BackgroundSetSupplier<T> {

    T get() throws BackgroundStackContainerException;
}
