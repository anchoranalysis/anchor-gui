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

package org.anchoranalysis.gui.interactivebrowser.filelist;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.opened.OpenedFileGUIMultipleDropDown;
import org.apache.commons.lang3.ArrayUtils;

public class InteractiveFileListMouseListener extends MouseAdapter {

    private AddVideoStatsModule adder;
    private InteractiveFileListTableModel tableModel;
    private IOpenFile fileOpenManager;
    private VideoStatsModuleGlobalParams mpg;
    private MarkDisplaySettings markDisplaySettings;

    public InteractiveFileListMouseListener(
            AddVideoStatsModule adder,
            InteractiveFileListTableModel tableModel,
            IOpenFile fileOpenManager,
            VideoStatsModuleGlobalParams mpg,
            MarkDisplaySettings markDisplaySettings) {
        super();
        this.adder = adder;
        this.tableModel = tableModel;
        this.fileOpenManager = fileOpenManager;
        this.mpg = mpg;
        this.markDisplaySettings = markDisplaySettings;
    }

    // NB  Popup can occur on mousePressed (Linux,MacOS)  or on mouseReleased (Windows)
    // Double-click is mousePressed on all of the above OSes

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e, true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e, false);
    }

    private void maybeShowPopup(MouseEvent e, boolean includeDoubleClick) {

        if (e.isPopupTrigger()) {

            maybeSelectCurrentRow(e);

            doPopup(e);
            return;
        }

        if (includeDoubleClick && e.getClickCount() >= 2) {
            doDefaultOperationOnSelection(e);
        }
    }

    private void maybeSelectCurrentRow(MouseEvent e) {
        JTable target = (JTable) e.getSource();

        int r = target.rowAtPoint(e.getPoint());

        if (!isRowSelected(target, r)) {
            // If row isn't already selected there is no need for us to select it
            setSelectionToSingleRow(target, r);
        }
    }

    private static void setSelectionToSingleRow(JTable table, int rowIndex) {

        if (rowIndex >= 0 && rowIndex < table.getRowCount()) {
            table.setRowSelectionInterval(rowIndex, rowIndex);
        } else {
            table.clearSelection();
        }
    }

    private static boolean isRowSelected(JTable table, int rowIndex) {
        return ArrayUtils.contains(table.getSelectedRows(), rowIndex);
    }

    private void doPopup(MouseEvent e) {
        JTable target = (JTable) e.getSource();

        if (target.getSelectedRowCount() == 0) {
            // We do nothing
        } else if (target.getSelectedRowCount() == 1) {
            singleSelectionPopup(target, e);
        } else {
            multipleSelectionPopup(target, e);
        }
    }

    private void doDefaultOperationOnSelection(MouseEvent e) {
        JTable target = (JTable) e.getSource();

        if (target.getSelectedRowCount() == 0) {
            // We do nothing
        } else if (target.getSelectedRowCount() == 1) {
            singleSelectionDefaultOperation(target, e);
        } else {
            singleSelectionDefaultOperation(target, e);
        }
    }

    private void singleSelectionDefaultOperation(JTable target, MouseEvent e) {
        int row = target.getSelectedRow();

        try {
            OpenedFile of = fileOpenManager.open(tableModel.get(row));
            OpenedFileGUI dropDown = of.getGUI();

            if (dropDown != null) {
                dropDown.executeDefaultOperation();
            }

        } catch (OperationFailedException exc) {
            mpg.getLogger()
                    .errorReporter()
                    .recordError(InteractiveFileListInternalFrame.class, exc);
        }
    }

    private void singleSelectionPopup(JTable target, MouseEvent e) {
        int row = target.getSelectedRow();

        try {
            OpenedFile of = fileOpenManager.open(tableModel.get(row));
            OpenedFileGUI dropDown = of.getGUI();

            if (dropDown != null) {
                dropDown.getPopup().show(e.getComponent(), e.getX(), e.getY());
            }

        } catch (OperationFailedException exc) {
            mpg.getLogger()
                    .errorReporter()
                    .recordError(InteractiveFileListInternalFrame.class, exc);
        }
    }

    private void multipleSelectionPopup(JTable target, MouseEvent e) {

        List<OpenedFileGUI> listSelectedDropDown = openAllSelected(target);

        if (listSelectedDropDown.size() == 0) {
            return;
        }

        // We create a special drop-down menu, that resembles the manifest dropdowns in the others
        //  but only shows items that exist in all

        OpenedFileGUIMultipleDropDown dropDown =
                new OpenedFileGUIMultipleDropDown(
                        adder, listSelectedDropDown, mpg, markDisplaySettings);
        dropDown.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
    }

    private List<OpenedFileGUI> openAllSelected(JTable target) {
        List<OpenedFileGUI> list = new ArrayList<>();
        for (int i : target.getSelectedRows()) {
            addIndice(list, i);
        }
        return list;
    }

    private void addIndice(List<OpenedFileGUI> listSelectedDropDown, int rowIndex) {
        InteractiveFile file = tableModel.get(rowIndex);
        if (file != null) {
            try {
                OpenedFile of = fileOpenManager.open(file);
                listSelectedDropDown.add(of.getGUI());
            } catch (OperationFailedException exc) {
                mpg.getLogger()
                        .errorReporter()
                        .recordError(InteractiveFileListInternalFrame.class, exc);
            }
        }
    }
}
