package org.anchoranalysis.gui.bean.filecreator;

import java.io.IOException;

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


import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.params.InputContextParams;

public class FileCreatorParams {

	// Params from InteractiveBrowserInput
	private RasterReader rasterReader;
	
	// Params from General Environment
	private VideoStatsModuleGlobalParams moduleParamsGlobal;
	
	public InputContextParams createInputContext() throws IOException {
		return moduleParamsGlobal.createInputContext();
	}
	
	private CacheMonitor cacheMonitor;
	private MarkEvaluatorManager markEvaluatorManager;
	private ImporterSettings importerSettings;

	public CacheMonitor getCacheMonitor() {
		return cacheMonitor;
	}
	public void setCacheMonitor(CacheMonitor cacheMonitor) {
		this.cacheMonitor = cacheMonitor;
	}
	public RasterReader getRasterReader() {
		return rasterReader;
	}
	public void setRasterReader(RasterReader rasterReader) {
		this.rasterReader = rasterReader;
	}
	public MarkEvaluatorManager getMarkEvaluatorManager() {
		return markEvaluatorManager;
	}
	public void setMarkEvaluatorManager(MarkEvaluatorManager markEvaluatorManager) {
		this.markEvaluatorManager = markEvaluatorManager;
	}
	public VideoStatsModuleGlobalParams getModuleParamsGlobal() {
		return moduleParamsGlobal;
	}
	public void setModuleParamsGlobal(
			VideoStatsModuleGlobalParams moduleParamsGlobal) {
		this.moduleParamsGlobal = moduleParamsGlobal;
	}
	public LogErrorReporter getLogErrorReporter() {
		return moduleParamsGlobal.getLogErrorReporter();
	}
	public ImporterSettings getImporterSettings() {
		return importerSettings;
	}
	public void setImporterSettings(ImporterSettings importerSettings) {
		this.importerSettings = importerSettings;
	}
	
	
}
