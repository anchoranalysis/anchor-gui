package org.anchoranalysis.gui.bean.filecreator;

import org.anchoranalysis.annotation.io.bean.input.AnnotationInputManager;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.annotation.AnnotationListInternalFrame;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.image.io.input.StackInputBase;

// A named channel collection derived from a file
public class AnnotationCreator extends FileCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int weightWidthDescription;

	public AnnotationCreator(int weightWidthDescription) {
		super();
		this.weightWidthDescription = weightWidthDescription;
	}
	
	// START BEAN PROPERTIES
	@BeanField
	private AnnotationInputManager<StackInputBase,?> input;
	// END BEAN PROPERTIES
	
	//private static Log log = LogFactory.getLog(NamedChnlCollectionCreator.class);

	@Override
	public VideoStatsModule createModule(String name, FileCreatorParams params,
			VideoStatsModuleGlobalParams mpg, IAddVideoStatsModule adder,
			IOpenFile fileOpenManager, ProgressReporter progressReporter)
			throws VideoStatsModuleCreateException {

		AnnotationListInternalFrame listFrame = new AnnotationListInternalFrame( name );
		
		try {
			listFrame.init(
				input,
				adder,
				fileOpenManager,
				params.getMarkCreatorParams(),
				progressReporter,
				weightWidthDescription
			);
		} catch (InitException e) {
			throw new VideoStatsModuleCreateException(e);
		}
		
		return listFrame.moduleCreator().createVideoStatsModule(new DefaultModuleState());
	}	

	@Override
	public String suggestName() {
				
		if (hasCustomName()) {
			return getCustomName();
		}
		
		return "untitled raster set";
	}

	public AnnotationInputManager<StackInputBase,?> getInput() {
		return input;
	}

	public void setInput(AnnotationInputManager<StackInputBase,?> input) {
		this.input = input;
	}
}
