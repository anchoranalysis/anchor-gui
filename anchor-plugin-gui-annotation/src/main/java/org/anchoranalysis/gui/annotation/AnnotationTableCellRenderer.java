package org.anchoranalysis.gui.annotation;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class AnnotationTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 386865384527171151L;

	private AnnotationTableModel model;
	
	private DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
	
	public AnnotationTableCellRenderer( AnnotationTableModel model ) {
		 setOpaque(true);
		 this.model = model;
	}
	
	@Override
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column==0) {
			standardCellApperance(label, SwingConstants.LEFT, isSelected);
			
		} else if (column==1 ){
			standardCellApperance(label, SwingConstants.RIGHT, isSelected);
			
		} else if (column==2) {
			
			// THIRD COLUMN (background color comiing from the Builder)
			FileAnnotationNamedChnlCollection fileAnnotation = model.getAnnotationProject().get(row);
			backgroundColor( label, fileAnnotation.summary().getColor() );
		}
		
		return label;
	}
	
	private static void standardCellApperance( JLabel label, int alignment, boolean isSelected ) {
		// FIRST COLUMN (name)
		label.setHorizontalAlignment(SwingConstants.LEFT);
		
		// We leave the default colors when the label isn't selected 
		if (!isSelected) {
			label.setForeground(Color.BLACK);
			label.setBackground(Color.WHITE);
		}
	}
	
	private static void backgroundColor( JLabel label, Color backgroundColor ) {
		label.setForeground(Color.BLACK);
		label.setText(" ");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setBackground( backgroundColor );		
	}
}