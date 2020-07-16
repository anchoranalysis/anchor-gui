/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.filelist;

import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.anchoranalysis.gui.cfgnrgtable.CellSelectedListener;
import org.anchoranalysis.gui.cfgnrgtable.TablePanel;

public class InteractiveFileListTablePanel {

    private TablePanel delegate;

    public InteractiveFileListTablePanel(TableModel tableModel) {

        delegate = new TablePanel("", tableModel, true);
    }

    public JPanel getPanel() {
        return delegate.getPanel();
    }

    public void addRowDoubleClickListener(CellSelectedListener l) {
        delegate.addRowDoubleClickListener(l);
    }

    public void removeRowDoubleClickListener(CellSelectedListener l) {
        delegate.removeRowDoubleClickListener(l);
    }

    public void addMouseListener(MouseListener l) {
        delegate.addMouseListener(l);
    }

    public void setHeaderVisible(boolean visible) {
        delegate.setHeaderVisible(visible);
    }

    public void selectAll() {
        delegate.selectAll();
    }

    public void clearSelection() {
        delegate.clearSelection();
    }

    public void setColumnWidth(int columnIndex, int width) {
        delegate.getTable().getColumnModel().getColumn(columnIndex).setPreferredWidth(width);
    }

    public void setColumnRenderer(int columnIndex, TableCellRenderer renderer) {
        delegate.getTable().getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }

    public void setBorder(Border border) {
        delegate.setBorder(border);
    }

    public void addTransferHandler(boolean dragEnabled, TransferHandler handler) {
        delegate.addTransferHandler(dragEnabled, handler);
    }
}
