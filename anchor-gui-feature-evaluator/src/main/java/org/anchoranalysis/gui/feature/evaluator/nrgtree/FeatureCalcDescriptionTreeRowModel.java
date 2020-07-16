/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import org.netbeans.swing.outline.RowModel;

public class FeatureCalcDescriptionTreeRowModel implements RowModel {

    @SuppressWarnings("rawtypes")
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
                // case 1:
                //   return Long.class;
            default:
                assert false;
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Value";
                // case 1:
                //   return Long.class;
            default:
                assert false;
        }
        return "untitled";
    }

    @Override
    public Object getValueFor(Object node, int column) {

        Node nodeC = (Node) node;

        // File f = (File) node;
        switch (column) {
            case 0:
                return nodeC.getValue();
                // case 1:
                //    return new Long(45);
            default:
                assert false;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    @Override
    public void setValueFor(Object node, int column, Object value) {
        // do nothing for now
    }
}
