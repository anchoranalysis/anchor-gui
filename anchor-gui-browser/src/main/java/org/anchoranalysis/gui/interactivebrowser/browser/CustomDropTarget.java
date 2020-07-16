/*-
 * #%L
 * anchor-gui-browser
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
