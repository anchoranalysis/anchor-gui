package org.anchoranalysis.gui.videostats.dropdown;

import java.awt.GraphicsConfiguration;
import java.nio.file.Path;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.plot.bean.colorscheme.GraphColorScheme;

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


import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.log.CommonContext;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.io.params.InputContextParams;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

// Globally available parameters for a VideoStatsModule
@RequiredArgsConstructor
public class VideoStatsModuleGlobalParams {

	// START BEAN PROPERTIES
	@Getter
	private final ExportPopupParams exportPopupParams;
	
	@Getter
	private final CommonContext context;
	
	@Getter
	private final InteractiveThreadPool threadPool;
	
	@Getter
	private final RandomNumberGenerator randomNumberGenerator;
	
	@Getter
	private final ExportTaskList exportTaskList;
	
	@Getter
	private final ColorIndex defaultColorIndexForMarks;
		
	@Getter
	private final GraphicsConfiguration graphicsCurrentScreen;
	// END BEAN PROPERTIES
	
	@Getter @Setter
	private GraphColorScheme graphColorScheme = new GraphColorScheme();
	
	@Getter
	private RegionMap regionMap = RegionMapSingleton.instance();	// For now we use global regionMaps
	
	public InputContextParams createInputContext() {
		return new InputContextParams();
	}
	
	public Logger getLogger() {
		return context.getLogger();
	}
	
	public Path getModelDirectory() {
		return context.getModelDirectory();
	}
}
