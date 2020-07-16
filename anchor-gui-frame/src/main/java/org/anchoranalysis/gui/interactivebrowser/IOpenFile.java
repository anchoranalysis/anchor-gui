/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.file.opened.OpenedFile;

public interface IOpenFile {

    OpenedFile open(InteractiveFile file) throws OperationFailedException;
}
