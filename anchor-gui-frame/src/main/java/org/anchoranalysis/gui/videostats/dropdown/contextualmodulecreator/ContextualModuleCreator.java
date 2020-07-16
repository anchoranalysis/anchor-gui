/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

// Creates a module in a given context (so the module creation might not occur, if bits are missing)
public abstract class ContextualModuleCreator {

    public abstract NamedModule[] create(
            String namePrefix,
            OperationWithProgressReporter<IAddVideoStatsModule, ? extends Throwable> adder,
            VideoStatsModuleGlobalParams mpg)
            throws CreateException;
}
