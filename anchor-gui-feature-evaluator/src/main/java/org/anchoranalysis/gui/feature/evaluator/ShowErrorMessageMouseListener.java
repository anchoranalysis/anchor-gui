/*-
 * #%L
 * anchor-gui-feature-evaluator
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
		
		if (e.getClickCount()>1) {
			
			final Outline outline = treeTable.getOutline();
			int selectedRow = outline.getSelectedRow();
			
			Node node = (Node) outline.getValueAt(selectedRow, 0);
			if (node!=null && node.hasError()) {
				JOptionPane.showMessageDialog(null, node.getErrorText() );
				
				errorReporter.recordError(FeatureEvaluatorTablePanel.class, node.getError());
			}
		}
	}
}
