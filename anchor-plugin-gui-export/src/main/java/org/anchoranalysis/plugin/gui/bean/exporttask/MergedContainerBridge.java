package org.anchoranalysis.plugin.gui.bean.exporttask;

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


import overlay.OverlayCollectionMarkFactory;

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.cache.CacheMonitor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.ContainerUtilities;
import org.anchoranalysis.gui.mergebridge.DualCfgNRGContainer;
import org.anchoranalysis.gui.mergebridge.MergeCfgBridge;
import org.anchoranalysis.gui.mergebridge.MergedColorIndex;
import org.anchoranalysis.gui.mergebridge.TransformToCfg;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.instantstate.CfgNRGNonHandleInstantState;
import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;
import ch.ethz.biol.cell.mpp.instantstate.OverlayedInstantState;
import ch.ethz.biol.cell.mpp.nrg.CfgNRG;
import ch.ethz.biol.cell.mpp.nrg.CfgWithNrgTotal;

class MergedContainerBridge implements IObjectBridge<ExportTaskParams,IBoundedIndexContainer<CfgNRGInstantState>> {

	private CacheMonitor cacheMonitor;
	
	public MergedContainerBridge() {
		super();
	}

	private BoundedIndexContainerBridgeWithoutIndex<OverlayedInstantState,CfgNRGInstantState> retBridge = null;
	
	@Override
	public IBoundedIndexContainer<CfgNRGInstantState> bridgeElement(
			ExportTaskParams sourceObject) throws GetOperationFailedException {

		// TODO fix
		// A container that supplies DualCfgInstantState
		if (retBridge==null) {
			DualCfgNRGContainer<Cfg> dualHistory = new DualCfgNRGContainer<>(
				ContainerUtilities.listCntrs( sourceObject.getAllFinderCfgNRGHistory() ),
				new TransformToCfg()
			);
			try {
				dualHistory.init( cacheMonitor );
			} catch (InitException e) {
				throw new GetOperationFailedException(e);
			}
			
			MergeCfgBridge mergeCfgBridge = new MergeCfgBridge();

			ColorIndex mergedColorIndex = new MergedColorIndex(mergeCfgBridge);

			
			// We map each DualCfgInstantState to a CfgInstantState
			IBoundedIndexContainer<OverlayedInstantState> cfgCntr = new BoundedIndexContainerBridgeWithoutIndex<>(
				dualHistory,
				mergeCfgBridge
			);
			
			// TODO HACK to allow exportparams to work
			sourceObject.setColorIndexMarks(mergedColorIndex);

			
			retBridge = new BoundedIndexContainerBridgeWithoutIndex<>(
				cfgCntr,
				s -> {
					Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays(s.getOverlayCollection());
					return new CfgNRGNonHandleInstantState(s.getIndex(), new CfgNRG( new CfgWithNrgTotal(cfg, null)) );
				}
			);
		}
		return retBridge;
	}

	public CacheMonitor getCacheMonitor() {
		return cacheMonitor;
	}

	public void setCacheMonitor(CacheMonitor cacheMonitor) {
		this.cacheMonitor = cacheMonitor;
	}
}