package org.anchoranalysis.gui.bean.exporttask;

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


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.anchoranalysis.core.error.reporter.ErrorReporter;

public class ExportTaskActionAsThread extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3944294960260017562L;
	
	private transient ExportTaskCommand command;	
	
	public static class ExportTaskCommand {
		private ExportTask exportTask;
		private ExportTaskParams exportTaskParams;
		private JFrame parentFrame;
		private boolean showMessageBoxes;
		private ErrorReporter errorReporter;
		
		public ExportTaskCommand(ExportTask exportTask,
				ExportTaskParams exportTaskParams, JFrame parentFrame, boolean showMessageBoxes, ErrorReporter errorReporter ) {
			super();
			this.exportTask = exportTask;
			this.exportTaskParams = exportTaskParams;
			this.parentFrame = parentFrame;
			this.showMessageBoxes = showMessageBoxes;
			this.errorReporter = errorReporter;
		}
		
		public void doTask() {
			
			String exportTaskTitle = "Export Task: " + exportTask.getBeanName();
			
			try {
				
				exportTask.init();
				
				int min = exportTask.getMinProgress(exportTaskParams);
				
				int max = exportTask.getMaxProgress(exportTaskParams);
				
				final ProgressMonitor progressMonitor = new ProgressMonitor(parentFrame, "Exporting", exportTask.getOutputName(), min, max);
				
				
				
				if (exportTask.execute(exportTaskParams,progressMonitor)) {
					
					if (showMessageBoxes) {
						JOptionPane.showMessageDialog(parentFrame, String.format("Export task '%s' succeeded", exportTask.getOutputName()), exportTaskTitle, JOptionPane.INFORMATION_MESSAGE );
					}
				} else {
					
					if (showMessageBoxes) {
						JOptionPane.showMessageDialog(parentFrame, String.format("Export task '%s' cancelled", exportTask.getOutputName()), exportTaskTitle, JOptionPane.ERROR_MESSAGE );
					}
				}
				
			} catch (ExportTaskFailedException e) {
				recordException(e, exportTaskTitle);
			}
		}
		
		private void recordException( Exception e, String exportTaskTitle ) {
			String newLine = System.getProperty("line.separator");
			if (showMessageBoxes) {
				JOptionPane.showMessageDialog(null, String.format("Export task '%s' failed: %s%s", exportTask.getOutputName(), newLine, e.toString()), exportTaskTitle, JOptionPane.ERROR_MESSAGE );
			} else {
				errorReporter.recordError(ExportTaskCommand.class, e);
			}
		}

		public ExportTask getExportTask() {
			return exportTask;
		}
	}
	
	public ExportTaskActionAsThread( ExportTaskCommand command ) {
		super( command.getExportTask().getOutputName() );
		this.command = command;
	}
	
	public ExportTaskActionAsThread(String label, ExportTaskCommand command) {
		super( label );
		this.command = command;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		Thread thread = new TaskThread(command);
		thread.start();
	}

	
	private static class TaskThread extends Thread {
		
		private ExportTaskCommand command;
		
		public TaskThread(ExportTaskCommand command) {
			super();
			this.command = command;
		}

		@Override
		public void run() {
			command.doTask();
		}
		
	}

}
