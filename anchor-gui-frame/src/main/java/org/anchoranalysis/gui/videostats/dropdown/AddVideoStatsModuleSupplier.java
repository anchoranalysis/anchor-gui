package org.anchoranalysis.gui.videostats.dropdown;

import org.anchoranalysis.core.progress.CachedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;

@FunctionalInterface
public interface AddVideoStatsModuleSupplier {

    AddVideoStatsModule get(ProgressReporter progressReporter) throws BackgroundStackContainerException;
    
    public static AddVideoStatsModuleSupplier cache(AddVideoStatsModuleSupplier supplier) {
        return CachedProgressingSupplier.cache(supplier::get)::get;
    }
}
