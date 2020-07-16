/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.InteractiveFileListTableModel;

public class AnnotationTableModel extends InteractiveFileListTableModel {

    private OperationWithProgressReporter<AnnotationProject, OperationFailedException>
            opAnnotationProject;
    private AnnotationProject annotationProject;

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

                    FileAnnotationNamedChnlCollection fileAnnotation =
                            annotationProject.get(rowIndex);

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
            OperationWithProgressReporter<AnnotationProject, OperationFailedException>
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
        this.annotationProject = opAnnotationProject.doOperation(progressReporter);
        this.annotationProject.addAnnotationChangedListener(
                new AnnotationChangedListener() {

                    @Override
                    public void annotationChanged(int index) {
                        tableModel.fireTableRowsUpdated(index, index);
                    }
                });
    }

    @Override
    public TableModel getTableModel() {
        return tableModel;
    }

    public AnnotationProject getAnnotationProject() {
        return annotationProject;
    }

    @Override
    public void fireTableDataChanged() {
        tableModel.fireTableDataChanged();
        ;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        tableModel.addTableModelListener(l);
    }
}
