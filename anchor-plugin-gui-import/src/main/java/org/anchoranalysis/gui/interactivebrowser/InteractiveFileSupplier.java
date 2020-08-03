package org.anchoranalysis.gui.interactivebrowser;

import java.util.List;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;

@FunctionalInterface
public interface InteractiveFileSupplier {

    List<InteractiveFile> get(ProgressReporter progressReporter) throws OperationFailedException;
}
