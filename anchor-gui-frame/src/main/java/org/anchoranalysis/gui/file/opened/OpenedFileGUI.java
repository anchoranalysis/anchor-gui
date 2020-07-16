/* (C)2020 */
package org.anchoranalysis.gui.file.opened;

import org.anchoranalysis.gui.file.interactive.InteractiveFile;

public class OpenedFileGUI extends OpenedFile {

    private IOpenedFileGUI openedFileGUI;
    private InteractiveFile file;

    public OpenedFileGUI(InteractiveFile file, IOpenedFileGUI openedFileGUI) {
        super();
        this.openedFileGUI = openedFileGUI;
        this.file = file;
    }

    @Override
    public IOpenedFileGUI getGUI() {
        return openedFileGUI;
    }

    public InteractiveFile getFile() {
        return file;
    }
}
