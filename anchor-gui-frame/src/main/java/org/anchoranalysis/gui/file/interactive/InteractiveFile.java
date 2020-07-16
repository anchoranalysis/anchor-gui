/* (C)2020 */
package org.anchoranalysis.gui.file.interactive;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// An entity that can be opened in the InteractiveBrowser
public abstract class InteractiveFile implements Comparable<InteractiveFile> {

    public abstract String identifier();

    // A file associated with this item
    public abstract Optional<File> associatedFile();

    public abstract String type();

    public abstract OpenedFile open(
            final IAddVideoStatsModule globalSubgroupAdder,
            final BoundOutputManagerRouteErrors outputManager)
            throws OperationFailedException;

    @Override
    public int compareTo(InteractiveFile arg0) {
        assert (arg0.identifier() != null);
        assert (identifier() != null);
        return identifier().compareTo(arg0.identifier());
    }
}
