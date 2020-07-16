/* (C)2020 */
package org.anchoranalysis.gui.cfgnrgtable;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.anchoranalysis.core.color.RGBColor;

public class ColorRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 386865384527171151L;

    public ColorRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
        RGBColor newColor = (RGBColor) color;

        /*setToolTipText("RGB value: " + newColor.getRed() + ", "
                  + newColor.getGreen() + ", "
                  + newColor.getBlue());

        setBackground(newColor.toAWTColor());
        setForeground(newColor.toAWTColor());
        setText("a");*/

        setBackground(newColor.toAWTColor());

        return this;
    }
}
