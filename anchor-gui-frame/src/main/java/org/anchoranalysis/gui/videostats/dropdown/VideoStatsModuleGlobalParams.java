package org.anchoranalysis.gui.videostats.dropdown;

import java.awt.GraphicsConfiguration;
import java.io.IOException;

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
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.videostats.threading.InteractiveThreadPool;
import org.anchoranalysis.io.params.InputContextParams;
import org.anchoranalysis.plugin.gui.bean.exporttask.ExportTaskList;

// Globally available parameters for a VideoStatsModule
public class VideoStatsModuleGlobalParams {

	private ExportPopupParams exportPopupParams;
	private LogErrorReporter logErrorReporter;
	private InteractiveThreadPool threadPool;
	private RandomNumberGenerator randomNumberGenerator;
	private ExportTaskList exportTaskList;
	private ColorIndex defaultColorIndexForMarks;
	private GraphColorScheme graphColorScheme = new GraphColorScheme();
	private RegionMap regionMap = RegionMapSingleton.instance();	// For now we use global regionMaps
	private GraphicsConfiguration graphicsCurrentScreen;
	
	
	public InputContextParams createInputContext() throws IOException {
		return new InputContextParams();
	}
	
	public ExportPopupParams getExportPopupParams() {
		return exportPopupParams;
	}
	public void setExportPopupParams(ExportPopupParams exportPopupParams) {
		this.exportPopupParams = exportPopupParams;
	}
	public LogErrorReporter getLogErrorReporter() {
		return logErrorReporter;
	}
	public void setLogErrorReporter(LogErrorReporter logErrorReporter) {
		this.logErrorReporter = logErrorReporter;
	}
	public InteractiveThreadPool getThreadPool() {
		return threadPool;
	}
	public void setThreadPool(InteractiveThreadPool threadPool) {
		this.threadPool = threadPool;
	}
	public RandomNumberGenerator getRandomNumberGenerator() {
		return randomNumberGenerator;
	}
	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}
	public ExportTaskList getExportTaskList() {
		return exportTaskList;
	}
	public void setExportTaskList(ExportTaskList exportTaskList) {
		this.exportTaskList = exportTaskList;
	}
	public GraphColorScheme getGraphColorScheme() {
		return graphColorScheme;
	}
	public void setGraphColorScheme(GraphColorScheme graphColorScheme) {
		this.graphColorScheme = graphColorScheme;
	}
	public ColorIndex getDefaultColorIndexForMarks() {
		return defaultColorIndexForMarks;
	}
	public void setDefaultColorIndexForMarks(ColorIndex colorIndex) {
		this.defaultColorIndexForMarks = colorIndex;
	}
	public RegionMap getRegionMap() {
		return regionMap;
	}
	public GraphicsConfiguration getGraphicsCurrentScreen() {
		return graphicsCurrentScreen;
	}
	public void setGraphicsCurrentScreen(GraphicsConfiguration graphicsCurrentScreen) {
		this.graphicsCurrentScreen = graphicsCurrentScreen;
	}
}
