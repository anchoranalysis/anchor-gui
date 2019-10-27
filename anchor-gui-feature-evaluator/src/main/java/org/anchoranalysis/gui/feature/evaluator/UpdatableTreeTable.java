package org.anchoranalysis.gui.feature.evaluator;

/*-
 * #%L
 * anchor-gui-feature-evaluator
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

	/** Adds the tree-table to the split-pane */
	public void addToSplitPane( JSplitPane splitPane ) {
		splitPane.add( treeTable.getComponent() );
	    
	    treeTable.resizeColumns();
		splitPane.setResizeWeight(0.2);
	}

	public SinglePairUpdater getUpdater() {
		return updater;
	}
}
