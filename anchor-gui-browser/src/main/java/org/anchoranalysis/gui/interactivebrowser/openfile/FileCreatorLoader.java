package org.anchoranalysis.gui.interactivebrowser.openfile;

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


import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.FileCreatorParams;
import org.anchoranalysis.gui.interactivebrowser.FileOpenManager;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.progressreporter.ProgressReporterInteractiveWorker;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.threading.InteractiveWorker;
import org.apache.commons.lang.time.StopWatch;

public class FileCreatorLoader {

	private FileCreatorParams params;

	private FileOpenManager fileOpenManager;
	private SubgrouppedAdder globalSubgroupAdder;
	private VideoStatsModuleGlobalParams mpg;
	
	public FileCreatorLoader(
		FileCreatorParams params,
		FileOpenManager fileOpenManager,
		SubgrouppedAdder globalSubgroupAdder
	) {
		super();
		this.params = params;
		this.mpg = params.getMarkCreatorParams().getModuleParams();
		this.fileOpenManager = fileOpenManager;
		this.globalSubgroupAdder = globalSubgroupAdder;
	}
	
	public void addFileListSummaryModule( List<FileCreator> listFileCreator, Component parentComponent ) {
		for( FileCreator fc : listFileCreator ) {
			addSingleCreator( fc, parentComponent );
		}
	}
	
	private void addSingleCreator( FileCreator fileCreator, Component parentComponent ) {

//		ProgressMonitor progressMonitor = new ProgressMonitor(ProgressMonitorExample.this,
//                "Operation in progress...",
//                "", 0, 100);
		
		String name = fileCreator.suggestName();
		String threadDescription = String.format("Loading File Creator '%s'", name);
		
		SwingUtilities.invokeLater( () -> {
			Loader loader = new Loader( new NamedBean<>(name, fileCreator), parentComponent );
			mpg.getThreadPool().submitWithProgressMonitor(loader, threadDescription);
		});
	}

	
	private class Loader extends InteractiveWorker<Integer, Void> {
		
		private NamedBean<FileCreator> fileCreator;
		private Component parentComponent;
		
		public Loader(NamedBean<FileCreator> fileCreator, Component parentComponent) {
			super();
			this.fileCreator = fileCreator;
			this.parentComponent = parentComponent;
		}

		@Override
		protected Integer doInBackground() {
			return 0;
		}

		private VideoStatsModule createModule() {

			StopWatch timer = new StopWatch();
			timer.start();
			
			String name = fileCreator.getValue().suggestName();
			
			try (ProgressReporter progressReporter = new ProgressReporterInteractiveWorker(this)) {
								
				VideoStatsModule module = fileCreator.getValue().createModule( name, params, mpg, globalSubgroupAdder.createChild(), fileOpenManager, progressReporter );
				
				mpg.getLogErrorReporter().getLogReporter().logFormatted("Loaded fileListSummaryModule %s (%dms)", name, timer.getTime() );
				
				return module;
				
			} catch (VideoStatsModuleCreateException e) {
				// Should we change this to the error reporter?
				mpg.getLogErrorReporter().getLogReporter().logFormatted("Failed to load fileListSummaryModule after %s (%dms)", name, timer.getTime() );
				mpg.getLogErrorReporter().getErrorReporter().recordError(FileCreatorLoader.class, e);
				
				 //+ ExceptionUtils.getFullStackTrace(e)
				JOptionPane.showMessageDialog(parentComponent, "An error occurred while loading module. See log." );
				
				return null;
			}
		}
		
		@Override
		protected void done() {
			
			VideoStatsModule module = createModule();
			if (module!=null) {
				globalSubgroupAdder.addVideoStatsModule( module );
			}
			
		}
		
	}


	public ImporterSettings getImporterSettings() {
		return params.getImporterSettings();
	}
}
