package org.anchoranalysis.gui.kernel;

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


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNodeImpl;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.cfgnrg.StatePanel;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.videostats.internalframe.IColoredCfgUpdater;

import overlay.OverlayMark;
import ch.ethz.biol.cell.gui.overlay.ColoredOverlayCollection;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.imageprocessing.io.generator.raster.OverlayedDisplayStackUpdate;
import ch.ethz.biol.cell.mpp.mark.Mark;

public class ProposerFailureDescriptionPanel extends StatePanel<ProposerFailureDescription> {

	private JScrollPane scrollPane;
	
	private JTree tree;
	
	private JPanel panel;
	private JPanel buttonPanel;
	
	private IColoredCfgUpdater setCfg;
	
	// If set to a string, we always expand a node of this name
	// If empty we expand all nodes
	private String alwaysExpandNode = "";
	
	public ErrorNodeImpl getSelectedNode() {
		if (tree.getSelectionPath()!=null) {
			Object o = tree.getSelectionPath().getLastPathComponent();
			ErrorNodeImpl node = (ErrorNodeImpl) o;
			return node;	
		} else {
			return null;
		}
	}
	
	private class SetExpandAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SetExpandAction() {
			super("Set as Always Expand");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {

			// In case nothing is selected on the tree
			if (tree.getSelectionPath()!=null) {
				Object o = tree.getSelectionPath().getLastPathComponent();
				ErrorNodeImpl node = (ErrorNodeImpl) o;
				alwaysExpandNode = node.getErrorMessage();	
			} else {
				alwaysExpandNode = "";
			}
			
			doExpand();
		}
		
	}
	
	private class ClearExpandAction extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ClearExpandAction() {
			super("Clear Always Expand");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			alwaysExpandNode = "";
			doExpand();
		}
		
	}
	
	private class ShowAssociatedMark extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6960802322853127219L;

		public ShowAssociatedMark() {
			super("Show Associated Mark");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			if (tree.getSelectionPath()!=null) {
				Object o = tree.getSelectionPath().getLastPathComponent();
				ErrorNodeImpl node = (ErrorNodeImpl) o;
				
				if (node.getAssociatedMark()!=null) {
					
					ColoredOverlayCollection coc = createOverlayForMark( node.getAssociatedMark() );
					
					OverlayedDisplayStackUpdate update = OverlayedDisplayStackUpdate.assignOverlays(coc);
					setCfg.applyUpdate( update );
				}
			}
		}
	}
	
	
	private static ColoredOverlayCollection createOverlayForMark( Mark mark ) {
		
		Mark dup = mark.duplicate();
		dup.setId(0);
		
		OverlayMark ol = new OverlayMark(dup);
		
		ColoredOverlayCollection coc = new ColoredOverlayCollection();
		coc.add( ol, new RGBColor(Color.YELLOW) );
		return coc;
	}
	
	
	
	public ProposerFailureDescriptionPanel( IColoredCfgUpdater setCfg ) {
		
		this.panel = new JPanel();
		
		this.tree = new JTree( new DefaultTreeModel(null) );
		this.tree.setRootVisible(false);
		
		this.scrollPane = new JScrollPane(this.tree);
		this.panel.setLayout( new BorderLayout() );
		this.panel.setBorder( BorderFactory.createEmptyBorder() );
		this.panel.add( scrollPane, BorderLayout.CENTER );

		this.setCfg = setCfg;
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout( new FlowLayout() );
		buttonPanel.setBorder( BorderFactory.createEmptyBorder() );
		buttonPanel.add( new JButton( new SetExpandAction() ) );
		buttonPanel.add( new JButton( new ClearExpandAction() ) );
		
		if (this.setCfg!=null) {
			buttonPanel.add( new JButton( new ShowAssociatedMark() ) );
		}
		
		this.panel.add( buttonPanel, BorderLayout.SOUTH );
		
		this.panel.setFocusable(false);
		this.scrollPane.setFocusable(false);
		this.tree.setFocusable(false);
	}

	
	public void addAction( AbstractAction a ) {
		buttonPanel.add( new JButton( a) );
	}
	
	@Override
	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void updateState(ProposerFailureDescription state)
			throws StatePanelUpdateException {

		if (state!=null) {
			this.tree.setModel( state.getErrorTree() );
			doExpand();
		} else {
			this.tree.setModel( null );
		}
	}
	
	private static void expandAll( JTree jTree ) {
		for (int i = 0; i < jTree.getRowCount(); i++) {
		    jTree.expandRow(i);
		}
	}

	private void expandNodes( JTree jTree, String nodeName ) {
		
		TreeModel model = jTree.getModel();
		if (model != null) {
			ErrorNodeImpl root = (ErrorNodeImpl) model.getRoot();
			System.out.println(root.toString());
			walk(jTree,root, nodeName, new TreePath(root) );
		}
		else {
			System.out.println("Tree is empty.");
		}
	}


	private static class CustomTreePath extends TreePath {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3468302669721513132L;

		public CustomTreePath(TreePath parent, Object lastElement) {
			super(parent, lastElement);
			// TODO Auto-generated constructor stub
		}
		
		
	}
	
	protected void walk(JTree jTree, ErrorNodeImpl o, String nodeName, TreePath path ){
		
		if (o.getErrorMessage().equals(nodeName)) {
					// make sure this and all parent nodes are expanded
			expandNodeUntilParent( tree, o, path );
			return;
		}
		
		TreeModel model = jTree.getModel();
		int cc;
		cc = model.getChildCount(o);
		for( int i=0; i < cc; i++) {
			ErrorNodeImpl child = (ErrorNodeImpl) model.getChild(o, i );
		
			if (!model.isLeaf(child)) {
				walk(jTree,child,nodeName, new CustomTreePath(path,child) );
			}
		}
	}

	
	private void expandNodeUntilParent( JTree tree, ErrorNodeImpl node, TreePath path ) {
		tree.expandPath(path);
	}
	
	private void doExpand() {
		
		if (alwaysExpandNode.isEmpty()) {
			expandAll(this.tree);
		} else {
			expandNodes(this.tree, alwaysExpandNode);
		}
	}
	
	public void setMaximumSize(Dimension maximumSize) {
		panel.setMaximumSize(maximumSize);
	}


	public void setMinimumSize(Dimension minimumSize) {
		panel.setMinimumSize(minimumSize);
	}


	public void setPreferredSize(Dimension preferredSize) {
		panel.setPreferredSize(preferredSize);
	}

	
	@Override
	public IPropertyValueSendable<IntArray> getSelectMarksSendable() {
		return null;
	}

	@Override
	public IPropertyValueReceivable<IntArray> getSelectMarksReceivable() {
		return null;
	}

	@Override
	public IPropertyValueReceivable<OverlayCollection> getSelectOverlayCollectionReceivable() {
		return null;
	}

	@Override
	public IPropertyValueReceivable<Integer> getSelectIndexReceivable() {
		return null;
	}
}
