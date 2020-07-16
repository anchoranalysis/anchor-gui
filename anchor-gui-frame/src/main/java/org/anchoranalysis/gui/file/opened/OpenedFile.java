/* (C)2020 */
package org.anchoranalysis.gui.file.opened;

import org.anchoranalysis.gui.file.interactive.InteractiveFile;

public abstract class OpenedFile {

    public abstract InteractiveFile getFile();

    public abstract IOpenedFileGUI getGUI();
}
