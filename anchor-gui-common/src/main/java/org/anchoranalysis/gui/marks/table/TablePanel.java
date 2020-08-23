/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.marks.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.table.TableModel;

public class TablePanel {

    private JPanel panel;

    private JTable table;

    private ArrayList<CellSelectedListener> eventRowDoubleClickedListeners = new ArrayList<>();

    // Empty title hides the title
    public TablePanel(String title, TableModel tableModel, boolean scrollable) {
        panel = new JPanel();

        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 2, 0));
        panel.setLayout(new BorderLayout());

        if (!title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        table = new JTable(tableModel);

        if (scrollable) {
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);
            panel.add(scrollPane, BorderLayout.CENTER);
        } else {
            panel.add(table, BorderLayout.CENTER);
        }

        table.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            JTable target = (JTable) e.getSource();
                            int row = target.getSelectedRow();
                            int column = target.getSelectedColumn();

                            for (CellSelectedListener l : eventRowDoubleClickedListeners) {
                                l.cellSelected(row, column);
                            }
                        }
                    }
                });
    }

    public void addTransferHandler(boolean dragEnabled, TransferHandler handler) {
        // Enable drag and dropping
        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(handler);
    }

    public JPanel getPanel() {
        return panel;
    }

    public JTable getTable() {
        return table;
    }

    public void addMouseListener(MouseListener l) {
        table.addMouseListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        table.removeMouseListener(l);
    }

    public void addRowDoubleClickListener(CellSelectedListener l) {
        eventRowDoubleClickedListeners.add(l);
    }

    public void removeRowDoubleClickListener(CellSelectedListener l) {
        eventRowDoubleClickedListeners.remove(l);
    }

    public void setHeaderVisible(boolean visible) {
        if (table.getTableHeader().isVisible() != visible) {
            table.getTableHeader().setVisible(visible);
            table.getTableHeader().setPreferredSize(visible ? null : new Dimension(-1, 0));
        }
    }

    public void selectAll() {
        table.selectAll();
    }

    public void clearSelection() {
        table.clearSelection();
    }

    public void setBorder(Border border) {
        panel.setBorder(border);
    }
}
