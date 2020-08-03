package org.anchoranalysis.gui.series;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.CachedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;

@FunctionalInterface
public interface TimeSequenceProviderSupplier {

    TimeSequenceProvider get(ProgressReporter progressReporter) throws CreateException;
    
    public static TimeSequenceProviderSupplier cache(TimeSequenceProviderSupplier supplier) {
        return CachedProgressingSupplier.cache(supplier::get)::get;
    }
}
