package org.anchoranalysis.plugin.gui.bean.exporttask;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;

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

public class ExportTaskMergedCfgNRGInstantState extends ExportTaskRasterGeneratorFromBoundedIndexContainer<CfgNRGInstantState> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8647428281002579676L;
	
	// START BEAN PROPERTIES
	
	// END BEAN PROPERTIES
	
	// TRANSIENT STATE
	private MergedContainerBridge containerBridge;
	
	public ExportTaskMergedCfgNRGInstantState() {
		super();
	}
	
	public void init() {
		containerBridge = new MergedContainerBridge(
			() -> RegionMapSingleton.instance().membershipWithFlagsForIndex(
				GlobalRegionIdentifiers.SUBMARK_INSIDE
			)	
		);
		super.setBridge(containerBridge);
	}
}
