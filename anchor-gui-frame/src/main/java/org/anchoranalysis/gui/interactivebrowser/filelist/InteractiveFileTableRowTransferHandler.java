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
