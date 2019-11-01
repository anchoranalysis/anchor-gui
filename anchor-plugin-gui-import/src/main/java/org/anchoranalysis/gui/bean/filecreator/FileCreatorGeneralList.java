package org.anchoranalysis.gui.bean.filecreator;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.interactivebrowser.filelist.SimpleInteractiveFileListInternalFrame;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public abstract class FileCreatorGeneralList extends FileCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public abstract void addFilesToList(
		List<InteractiveFile> listFiles,
		FileCreatorParams params, ProgressReporter progressReporter
	) throws CreateException;
	
	public VideoStatsModule createModule( String name, final FileCreatorParams params, VideoStatsModuleGlobalParams mpg, IAddVideoStatsModule adder, IOpenFile fileOpenManager, final ProgressReporter progressReporter ) throws VideoStatsModuleCreateException {
		
		// Operation to retrieve files
		OperationWithProgressReporter<List<InteractiveFile>> op = new OperationWithProgressReporter<List<InteractiveFile>>() {

			@Override
			public List<InteractiveFile> doOperation( ProgressReporter progressReporter ) throws ExecuteException {
				
				List<InteractiveFile> listFiles = new ArrayList<>(); 
				try {
					addFilesToList(listFiles, params, progressReporter);
				} catch (CreateException e) {
					throw new ExecuteException(e);
				}
				
				// Let's sort out list before we display them
				Collections.sort(listFiles);
				return listFiles;
			}
			
		};
				
		SimpleInteractiveFileListInternalFrame manifestListSummary = new SimpleInteractiveFileListInternalFrame( name );
		try {
			manifestListSummary.init(
				adder,
				op,
				fileOpenManager,
				mpg,
				params.getMarkCreatorParams().getMarkDisplaySettings(),
				progressReporter
			);
		} catch (InitException e) {
			throw new VideoStatsModuleCreateException(e);
		}
		return manifestListSummary.createVideoStatsModule(new DefaultModuleState());
	}
}
