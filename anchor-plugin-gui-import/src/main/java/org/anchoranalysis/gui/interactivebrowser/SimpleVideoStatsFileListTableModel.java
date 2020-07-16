/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser;

import java.util.List;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.InteractiveFileListTableModel;

public class SimpleVideoStatsFileListTableModel extends InteractiveFileListTableModel {

    private List<InteractiveFile> fileInputList;
    private OperationWithProgressReporter<List<InteractiveFile>, OperationFailedException>
            opFileInputList;

    private AbstractTableModel tableModel =
            new AbstractTableModel() {

                private static final long serialVersionUID = 1L;

                @Override
                public int getColumnCount() {
                    return 1;
                }

                @Override
                public int getRowCount() {
                    return fileInputList.size();
                }

                @Override
                public Object getValueAt(int row, int column) {

                    switch (column) {
                        case 0:
                            // NAME
                            return fileInputList.get(row).identifier();
                        default:
                            return "error";
                    }
                }

                @Override
                public String getColumnName(int col) {

                    switch (col) {
                        case 0:
                            return "name";
                        default:
                            assert false;
                            return "error";
                    }
                }

                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public Class getColumnClass(int c) {

                    switch (c) {
                        case 0:
                            return String.class;
                        default:
                            throw new AnchorImpossibleSituationException();
                    }
                }
            };

    public SimpleVideoStatsFileListTableModel(
            OperationWithProgressReporter<List<InteractiveFile>, OperationFailedException>
                    opFileInputList,
            ProgressReporter progressReporter)
            throws OperationFailedException {
        this.opFileInputList = opFileInputList;
        refreshEntireTable(progressReporter);
        tableModel.fireTableDataChanged();
    }

    @Override
    public InteractiveFile get(int index) {
        return fileInputList.get(index);
    }

    // We don't automatically fireTableDataChanged after a refresh, as the two operations might be
    // called from different threads
    //   from a SwingWorker
    @Override
    public void refreshEntireTable(ProgressReporter progressReporter)
            throws OperationFailedException {
        this.fileInputList = opFileInputList.doOperation(progressReporter);
    }

    @Override
    public AbstractTableModel getTableModel() {
        return tableModel;
    }

    @Override
    public void fireTableDataChanged() {
        tableModel.fireTableDataChanged();
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModel.addTableModelListener(l);
    }
}
