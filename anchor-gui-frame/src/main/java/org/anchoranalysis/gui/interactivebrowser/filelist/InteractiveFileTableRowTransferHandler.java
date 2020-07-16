/*-
 * #%L
 * anchor-gui-frame
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
/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.filelist;

import java.awt.Cursor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;

public class InteractiveFileTableRowTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;

    // private final DataFlavor localObjectFlavor = new DataFlavor(Integer.class, "Integer Row
    // Index");
    private JTable table = null;
    private InteractiveFileListTableModel tableModel;

    public InteractiveFileTableRowTransferHandler(InteractiveFileListTableModel tableModel) {
        this.tableModel = tableModel;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable table = (JTable) c;

        int selectedIndex = table.getSelectedRow();

        assert (selectedIndex != -1);

        int[] selectedIndices = table.getSelectedRows();
        assert (selectedIndices.length > 0);

        InteractiveFile[] files = tableModel.get(selectedIndices);
        return new InteractiveFilesTransferable(files);
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info) {
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable t, int act) {
        if (act == TransferHandler.MOVE) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
