/*-
 * #%L
 * anchor-plugin-gui-annotation
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

package org.anchoranalysis.gui.annotation;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import lombok.Getter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.progress.CheckedProgressingSupplier;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.InteractiveFileListTableModel;

public class AnnotationTableModel implements InteractiveFileListTableModel {

    private CheckedProgressingSupplier<AnnotationProject, OperationFailedException>
            opAnnotationProject;
    @Getter private AnnotationProject annotationProject;

    private AbstractTableModel tableModel =
            new AbstractTableModel() {

                private static final long serialVersionUID = 1L;

                @Override
                public int getColumnCount() {
                    return 3;
                }

                @Override
                public int getRowCount() {
                    return annotationProject.size();
                }

                @Override
                public String getValueAt(int rowIndex, int columnIndex) {

                    FileAnnotationNamedChannels fileAnnotation = annotationProject.get(rowIndex);

                    switch (columnIndex) {
                        case 0:
                            // NAME
                            return fileAnnotation.identifier();
                        case 1:
                            return fileAnnotation.summary().getShortDescription();
                        case 2:
                            // COLUMN COUNT
                            return " "; // color column
                        default:
                            return "error";
                    }
                }

                @Override
                public String getColumnName(int col) {

                    switch (col) {
                        case 0:
                            return "name";
                        case 1:
                            return "count";
                        case 2:
                            return "annotation";
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
                        case 1:
                            return String.class;
                        default:
                            throw new AnchorImpossibleSituationException();
                    }
                }
            };

    public AnnotationTableModel(
            CheckedProgressingSupplier<AnnotationProject, OperationFailedException>
                    opAnnotationProject,
            ProgressReporter progressReporter)
            throws CreateException {
        this.opAnnotationProject = opAnnotationProject;
        try {
            refreshEntireTable(progressReporter);
            tableModel.fireTableDataChanged();
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public InteractiveFile get(int index) {
        return annotationProject.get(index);
    }

    @Override
    public void refreshEntireTable(ProgressReporter progressReporter)
            throws OperationFailedException {
        this.annotationProject = opAnnotationProject.get(progressReporter);
        this.annotationProject.addAnnotationChangedListener(
                index -> tableModel.fireTableRowsUpdated(index, index));
    }

    @Override
    public TableModel getTableModel() {
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
