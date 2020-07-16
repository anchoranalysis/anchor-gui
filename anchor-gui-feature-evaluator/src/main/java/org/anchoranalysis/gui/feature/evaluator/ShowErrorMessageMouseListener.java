/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.Node;
import org.anchoranalysis.gui.feature.evaluator.treetable.ITreeTableModel;
import org.netbeans.swing.outline.Outline;

class ShowErrorMessageMouseListener extends MouseAdapter {

    private ErrorReporter errorReporter;
    private ITreeTableModel treeTable;

    public ShowErrorMessageMouseListener(ITreeTableModel treeTable, ErrorReporter errorReporter) {
        super();
        this.errorReporter = errorReporter;
        this.treeTable = treeTable;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (e.getClickCount() > 1) {

            final Outline outline = treeTable.getOutline();
            int selectedRow = outline.getSelectedRow();

            Node node = (Node) outline.getValueAt(selectedRow, 0);
            if (node != null && node.hasError()) {
                JOptionPane.showMessageDialog(null, node.getErrorText());

                errorReporter.recordError(FeatureEvaluatorTablePanel.class, node.getError());
            }
        }
    }
}
