package org.anchoranalysis.plugin.gui.bean.exporttask;

/*-
 * #%L
 * anchor-plugin-gui-export
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.function.Supplier;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGNonHandleInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNrgTotal;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.core.bridge.BridgeElementException;
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

class MergedContainerBridge implements IObjectBridge<ExportTaskParams,IBoundedIndexContainer<CfgNRGInstantState>> {

	private CacheMonitor cacheMonitor;
	private Supplier<RegionMembershipWithFlags> regionMembership;
	
	public MergedContainerBridge( Supplier<RegionMembershipWithFlags> regionMembership ) {
		super();
		this.regionMembership = regionMembership;
	}

	private BoundedIndexContainerBridgeWithoutIndex<OverlayedInstantState,CfgNRGInstantState> retBridge = null;
	
	@Override
	public IBoundedIndexContainer<CfgNRGInstantState> bridgeElement(
			ExportTaskParams sourceObject) throws BridgeElementException {

		// TODO fix
		if (retBridge==null) {

			DualCfgNRGContainer<Cfg> dualHistory;
			
			try {
				dualHistory = new DualCfgNRGContainer<>(
					ContainerUtilities.listCntrs( sourceObject.getAllFinderCfgNRGHistory() ),
					new TransformToCfg()
				);
				
				dualHistory.init( cacheMonitor );
			} catch (InitException | GetOperationFailedException e) {
				throw new BridgeElementException(e);
			}
			
			MergeCfgBridge mergeCfgBridge = new MergeCfgBridge(regionMembership);

			ColorIndex mergedColorIndex = new MergedColorIndex(mergeCfgBridge);

			
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
