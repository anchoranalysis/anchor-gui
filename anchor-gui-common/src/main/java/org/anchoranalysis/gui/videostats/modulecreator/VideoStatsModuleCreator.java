/* (C)2020 */
package org.anchoranalysis.gui.videostats.modulecreator;

import java.awt.Component;
import java.util.Optional;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

// Responsible only for creating a module
public abstract class VideoStatsModuleCreator {

    public void beforeBackground(Component parentComponent) {}
    ;

    public abstract void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException;

    public void doInBackground(ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {}

    // If it returns empty(), no combining is possible. Override with operations
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return Optional.empty();
    }
}
