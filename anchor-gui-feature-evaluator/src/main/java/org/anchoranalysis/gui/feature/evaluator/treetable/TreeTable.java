/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.treetable;

import javax.swing.JComponent;
import javax.swing.tree.TreeModel;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;

public class TreeTable {

    private javax.swing.JScrollPane scrollPane;
    private org.netbeans.swing.outline.Outline outline;

    // renderDataProvider can be null
    public TreeTable(TreeModel treeMdl, TreeTableProperties properties) {
        scrollPane = new javax.swing.JScrollPane();

        // Create the Outline's model, consisting of the TreeModel and the RowModel,
        // together with two optional values: a boolean for something or other,
        // and the display name for the first column:
        OutlineModel mdl =
                DefaultOutlineModel.createOutlineModel(
                        treeMdl, properties.getRowModel(), true, properties.getTitle());

        // Initialize the Outline object:
        outline = new Outline();

        scrollPane.setName("jScrollPane1"); // NOI18N

        outline.setName("outline1"); // NOI18N
        scrollPane.setViewportView(outline);

        if (properties.getRenderDataProvider() != null) {
            outline.setRenderDataProvider(properties.getRenderDataProvider());
        }

        // By default, the root is shown, while here that isn't necessary:
        outline.setRootVisible(false);

        // Assign the model to the Outline object:
        outline.setModel(mdl);

        // Add the Outline object to the JScrollPane:
        scrollPane.setViewportView(outline);
    }

    public void resizeColumns() {
        // int valueWidth = 100;
        outline.getColumnModel().getColumn(0).setPreferredWidth(2000);
        outline.getColumnModel().getColumn(1).setPreferredWidth(1000);
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    public org.netbeans.swing.outline.Outline getOutline() {
        return outline;
    }
}
