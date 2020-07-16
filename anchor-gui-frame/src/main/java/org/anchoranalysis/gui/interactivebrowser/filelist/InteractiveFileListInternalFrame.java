/*-
 * #%L
 * anchor-gui-frame
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
package org.anchoranalysis.gui.interactivebrowser.filelist;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.table.TableCellRenderer;

import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.cfgnrgtable.CellSelectedListener;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.progressreporter.ProgressReporterInteractiveWorker;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;

public class InteractiveFileListInternalFrame {

	private JInternalFrame frame;
	private InteractiveFileListTablePanel tablePanel;
	private InteractiveFileListTableModel tableModel;
	private VideoStatsModuleGlobalParams mpg;
	
	public InteractiveFileListInternalFrame( String name ) {
		
		frame = new JInternalFrame(name);
		frame.setResizable(true);
		frame.setMaximizable(true);
		frame.setIconifiable(true);
		frame.setClosable(true);
		
		frame.setPreferredSize( new Dimension(480, 600 ));
		frame.setVisible(true);
	}
	
	public void init(
		final IAddVideoStatsModule adder,
		final InteractiveFileListTableModel tableModel,
		final IOpenFile fileOpenManager,
		final VideoStatsModuleGlobalParams mpg,
		MarkDisplaySettings markDisplaySettings
	) {
		
		assert( mpg.getExportPopupParams()!=null );
		
		this.tableModel = tableModel;
		this.mpg = mpg;
		
		tablePanel = new InteractiveFileListTablePanel( tableModel.getTableModel() );
		tablePanel.addTransferHandler(true, new InteractiveFileTableRowTransferHandler(tableModel) );
		
		tablePanel.setHeaderVisible(false);
		tablePanel.addMouseListener(
			new InteractiveFileListMouseListener(
				adder,
				tableModel,
				fileOpenManager,
				mpg,
				markDisplaySettings
			)
		);
		tablePanel.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		
		tablePanel.addRowDoubleClickListener( new CellSelectedListener() {
			
			@Override
			public void cellSelected(int row, int column) {
			}
		});
		frame.add(tablePanel.getPanel(), BorderLayout.CENTER);
		
		// Button Panel
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout( new FlowLayout(FlowLayout.RIGHT,7,0) );
		panelButtons.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		
		JButton buttonSelectAll = new JButton( new ActionSelectAll() ) ;
		JButton buttonSelectNone = new JButton( new ActionSelectNone() );
		JButton buttonRefresh = new JButton( new ActionRefresh() );
		
		buttonRefresh.setBorder( BorderFactory.createEmptyBorder(0, 5, 0, 5) );
		buttonSelectAll.setBorder( BorderFactory.createEmptyBorder(0, 5, 0, 5) );
		buttonSelectNone.setBorder( BorderFactory.createEmptyBorder(0, 5, 0, 5) );
		
		panelButtons.add( buttonRefresh );
		panelButtons.add( buttonSelectAll );
		panelButtons.add( buttonSelectNone );
		
		frame.add(panelButtons, BorderLayout.SOUTH);
	}

	public void addComponentTop( JComponent component ) {
		frame.add(component, BorderLayout.NORTH);
	}
	
	public void setColumnWidth( int columnIndex, int width ) {
		tablePanel.setColumnWidth(columnIndex, width);
	}
	
	private class ActionSelectAll extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ActionSelectAll() {
			super("All");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			tablePanel.selectAll();
		}
	}
	
	private class ActionSelectNone extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ActionSelectNone() {
			super("None");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			tablePanel.clearSelection();
		}
	}

	
	private class ActionRefresh extends AbstractAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ActionRefresh() {
			super("Refresh");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {

			InteractiveWorker<Integer, Void> worker = new InteractiveWorker<Integer, Void>() {

				@Override
				protected Integer doInBackground() throws Exception {
					try (ProgressReporter progressReporter = new ProgressReporterInteractiveWorker(this)) {
						progressReporter.open();
						tableModel.refreshEntireTable( progressReporter );
					}
					return 1;
				}

				@Override
				protected void done() {
					super.done();
					tableModel.fireTableDataChanged();
				}
			};
			
			mpg.getThreadPool().submitWithProgressMonitor(worker, "refresh interactiveFileList");
		}
	}
	
	// Get frame
	public JInternalFrame getFrame() {
		return frame;
	}

	public void setPreferredSize(Dimension arg0) {
		frame.setPreferredSize(arg0);
	}

	public void setMinimumSize(Dimension arg0) {
		frame.setMinimumSize(arg0);
	}
		
	public IModuleCreatorDefaultState moduleCreator() {
		return defaultFrameState-> {		
			VideoStatsModule module = new VideoStatsModule();
			module.setComponent( frame );
			module.setFixedSize(false);
			return module;
		};
	}

	public void setColumnRenderer(int columnIndex, TableCellRenderer renderer) {
		tablePanel.setColumnRenderer(columnIndex, renderer);
	}
}
