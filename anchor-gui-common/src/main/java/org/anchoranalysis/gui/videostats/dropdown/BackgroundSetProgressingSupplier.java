package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.progress.CachedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;

@FunctionalInterface
public interface BackgroundSetProgressingSupplier {

    BackgroundSet get(ProgressReporter progressReporter) throws BackgroundStackContainerException;
    
    public static BackgroundSetProgressingSupplier cache( BackgroundSetProgressingSupplier supplier ) {
        return CachedProgressingSupplier.cache( supplier::get )::get;
    }
}

