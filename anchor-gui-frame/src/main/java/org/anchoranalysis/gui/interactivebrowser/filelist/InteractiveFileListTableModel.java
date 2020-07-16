/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.filelist;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;

public abstract class InteractiveFileListTableModel {

    public abstract InteractiveFile get(int index);

    public InteractiveFile[] get(int[] indices) {
        InteractiveFile[] out = new InteractiveFile[indices.length];
        for (int i = 0; i < indices.length; i++) {
            out[i] = get(indices[i]);
        }
        return out;
    }

    public abstract void refreshEntireTable(ProgressReporter progressReporter)
            throws OperationFailedException;

    public abstract TableModel getTableModel();

    public abstract void fireTableDataChanged();

    public abstract void addTableModelListener(TableModelListener l);
}
