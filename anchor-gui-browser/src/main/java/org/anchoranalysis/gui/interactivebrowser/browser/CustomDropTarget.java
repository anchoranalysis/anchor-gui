/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.browser;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.interactivebrowser.openfile.OpenFile;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;

class CustomDropTarget extends DropTarget {
    private static final long serialVersionUID = 1L;

    private OpenFile openFileCreator;
    private Component parentComponent;
    private ImporterSettings importerSettings;
    private ErrorReporter errorReporter;

    public CustomDropTarget(
            OpenFile openFileCreator,
            Component parentComponent,
            ImporterSettings importerSettings,
            ErrorReporter errorReporter)
            throws HeadlessException {
        super();
        this.openFileCreator = openFileCreator;
        this.parentComponent = parentComponent;
        this.importerSettings = importerSettings;
        this.errorReporter = errorReporter;
    }

    public synchronized void drop(DropTargetDropEvent evt) {
        try {
            evt.acceptDrop(DnDConstants.ACTION_COPY);
            @SuppressWarnings("unchecked")
            List<File> droppedFiles =
                    (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            openFileCreator.openFileCreator(droppedFiles, null, parentComponent, importerSettings);
        } catch (Exception exc) {
            errorReporter.recordError(CustomDropTarget.class, exc);
        }
    }
}
