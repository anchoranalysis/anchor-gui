/* (C)2020 */
package org.anchoranalysis.gui.annotation;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class AnnotationTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 386865384527171151L;

    private AnnotationTableModel model;

    private DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();

    public AnnotationTableCellRenderer(AnnotationTableModel model) {
        setOpaque(true);
        this.model = model;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label =
                (JLabel)
                        defaultRenderer.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

        if (column == 0) {
            standardCellApperance(label, SwingConstants.LEFT, isSelected);

        } else if (column == 1) {
            standardCellApperance(label, SwingConstants.RIGHT, isSelected);

        } else if (column == 2) {

            // THIRD COLUMN (background color comiing from the Builder)
            FileAnnotationNamedChnlCollection fileAnnotation =
                    model.getAnnotationProject().get(row);
            backgroundColor(label, fileAnnotation.summary().getColor());
        }

        return label;
    }

    private static void standardCellApperance(JLabel label, int alignment, boolean isSelected) {
        // FIRST COLUMN (name)
        label.setHorizontalAlignment(SwingConstants.LEFT);

        // We leave the default colors when the label isn't selected
        if (!isSelected) {
            label.setForeground(Color.BLACK);
            label.setBackground(Color.WHITE);
        }
    }

    private static void backgroundColor(JLabel label, Color backgroundColor) {
        label.setForeground(Color.BLACK);
        label.setText(" ");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setBackground(backgroundColor);
    }
}
