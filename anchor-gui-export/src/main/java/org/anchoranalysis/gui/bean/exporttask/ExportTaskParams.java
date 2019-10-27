package org.anchoranalysis.gui.bean.exporttask;

import java.util.ArrayList;
import java.util.List;

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
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollection;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;


// Parameters an exportTask can draw itself from
public class ExportTaskParams {

	private ColorIndex colorIndexMarks;
	private FinderImgStackCollection finderImgStackCollection;
	private FinderCSVStats finderCsvStatistics;
	private List<ContainerGetter<CfgNRGInstantState>> listFinderCfgNRGHistory = new ArrayList<>();
	private CacheMonitor cacheMonitor;
	private BoundOutputManagerRouteErrors outputManager;

	public ColorIndex getColorIndexMarks() {
		return colorIndexMarks;
	}
	public void setColorIndexMarks(ColorIndex colorIndexMarks) {
		this.colorIndexMarks = colorIndexMarks;
	}
	public FinderImgStackCollection getFinderImgStackCollection() {
		return finderImgStackCollection;
	}
	public void setFinderImgStackCollection(FinderImgStackCollection finder) {
		this.finderImgStackCollection = finder;
	}
	public FinderCSVStats getFinderCsvStatistics() {
		return finderCsvStatistics;
	}
	public void setFinderCsvStatistics(FinderCSVStats csvStatistics) {
		this.finderCsvStatistics = csvStatistics;
	}
	
	public ContainerGetter<CfgNRGInstantState> getFinderCfgNRGHistory() {
		return listFinderCfgNRGHistory.get(0);
	}
	
	public ContainerGetter<CfgNRGInstantState> getFinderCfgNRGHistory(int index) {
		return listFinderCfgNRGHistory.get(index);
	}
	
	public List<ContainerGetter<CfgNRGInstantState>> getAllFinderCfgNRGHistory() {
		return listFinderCfgNRGHistory;
	}
	
	public int numCfgNRGHistory() {
		return listFinderCfgNRGHistory.size();
	}
	
	public void addFinderCfgNRGHistory(
			ContainerGetter<CfgNRGInstantState> finderCfgNRGHistory) {
		if (finderCfgNRGHistory!=null) {
			this.listFinderCfgNRGHistory.add( finderCfgNRGHistory );
		}
	}

	public CacheMonitor getCacheMonitor() {
		return cacheMonitor;
	}
	public void setCacheMonitor(CacheMonitor cacheMonitor) {
		this.cacheMonitor = cacheMonitor;
	}
	public BoundOutputManagerRouteErrors getOutputManager() {
		return outputManager;
	}
	public void setOutputManager(BoundOutputManagerRouteErrors outputManager) {
		this.outputManager = outputManager;
	}
}
