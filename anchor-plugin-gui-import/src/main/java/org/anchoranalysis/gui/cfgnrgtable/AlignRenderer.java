/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import javax.swing.table.DefaultTableCellRenderer;

public class AlignRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    public AlignRenderer(int align) {
        setHorizontalAlignment(align);
    }
}
