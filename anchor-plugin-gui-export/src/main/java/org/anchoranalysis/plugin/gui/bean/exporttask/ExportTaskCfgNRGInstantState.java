/*-
 * #%L
 * anchor-plugin-gui-export
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.plugin.gui.bean.exporttask;



import java.util.List;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;




import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.container.ContainerUtilities;
import org.anchoranalysis.gui.mergebridge.DualCfgNRGContainer;

public class ExportTaskCfgNRGInstantState extends ExportTaskRasterGeneratorFromBoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> {

	@Override
	public void init() {
		setBridge(this::convert);
	}
	
	private BoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> createPrimaryOnly(ExportTaskParams sourceObject) throws GetOperationFailedException {
		return new BoundedIndexContainerBridgeWithoutIndex<>(
			sourceObject.getFinderCfgNRGHistory().getCntr(),
			DualStateWithoutIndex::new
		);
	}
	
	private static DualCfgNRGContainer<CfgNRGInstantState> combine( List<ContainerGetter<CfgNRGInstantState>> cntrs ) throws GetOperationFailedException {
		
		DualCfgNRGContainer<CfgNRGInstantState> dualHistory = new DualCfgNRGContainer<>(
			ContainerUtilities.listCntrs(cntrs),
			a->a
		);
		
		dualHistory.init();
		return dualHistory;
	}
	
	private DualCfgNRGContainer<CfgNRGInstantState> mergedHistory(ExportTaskParams sourceObject) throws GetOperationFailedException {
		return combine(
			sourceObject.getAllFinderCfgNRGHistory()
		);
	}
	
	private BoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> createMergedBridge(ExportTaskParams sourceObject) throws GetOperationFailedException {

		// Otherwise we merge
		
		DualCfgNRGContainer<CfgNRGInstantState> dualHistory = mergedHistory(sourceObject);
		dualHistory.init();
		
		return new BoundedIndexContainerBridgeWithoutIndex<>(
			dualHistory,
			s -> new DualStateWithoutIndex<>( s.getList() )
		);
	}
	
	private BoundedIndexContainer<DualStateWithoutIndex<CfgNRGInstantState>> convert( ExportTaskParams sourceObject ) throws OperationFailedException {
		assert(sourceObject.numCfgNRGHistory()>0);
		
		try {
			if (sourceObject.numCfgNRGHistory()==1) {
				return createPrimaryOnly(sourceObject);
			} else {
				return createMergedBridge(sourceObject);
			}
		} catch (GetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
	}
}
