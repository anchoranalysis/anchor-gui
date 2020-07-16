/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import javax.swing.JSplitPane;
import org.anchoranalysis.gui.feature.evaluator.treetable.ITreeTableModel;

// An updatable tree-table model
class UpdatableTreeTable {
    private ITreeTableModel treeTable;
    private SinglePairUpdater updater;

    public UpdatableTreeTable(ITreeTableModel treeTable, SinglePairUpdater updater) {
        super();
        this.treeTable = treeTable;
        this.updater = updater;
    }

    public void addToSplitPane(JSplitPane splitPane) {
        splitPane.add(treeTable.getComponent());

        treeTable.resizeColumns();
        splitPane.setResizeWeight(0.2);
    }

    public SinglePairUpdater getUpdater() {
        return updater;
    }
}
