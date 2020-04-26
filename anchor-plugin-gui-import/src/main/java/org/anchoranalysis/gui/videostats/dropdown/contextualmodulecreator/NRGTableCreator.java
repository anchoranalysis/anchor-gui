package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;

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


import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.cfgnrg.StatePanelFrameHistoryCfgNRGInstantState;
import org.anchoranalysis.gui.cfgnrgtable.CfgNRGTablePanel;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleStateManager;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;

public class NRGTableCreator extends VideoStatsModuleCreatorContext {

	private final Operation<LoadContainer<CfgNRGInstantState>,GetOperationFailedException> operation;
	private final ColorIndex colorIndex;
	private final Operation<NRGStackWithParams,OperationFailedException> nrgStackWithParams;
	
	public NRGTableCreator(
		Operation<LoadContainer<CfgNRGInstantState>,GetOperationFailedException> operation,
		Operation<NRGStackWithParams,OperationFailedException> nrgStackWithParams,
		ColorIndex colorIndex
	) {
		super();
		this.operation = operation;
		this.colorIndex = colorIndex;
		this.nrgStackWithParams = nrgStackWithParams;
	}

	@Override
	public boolean precondition() {
		return (colorIndex!=null && nrgStackWithParams!=null);
	}

	@Override
	public IModuleCreatorDefaultState moduleCreator(DefaultModuleStateManager defaultStateManager, String namePrefix,
			VideoStatsModuleGlobalParams mpg) throws VideoStatsModuleCreateException {
		
		try {
			LoadContainer<CfgNRGInstantState> cntr = operation.doOperation();
			
			StatePanelFrameHistoryCfgNRGInstantState frame = new StatePanelFrameHistoryCfgNRGInstantState( namePrefix, !cntr.isExpensiveLoad() );
			frame.init(
				defaultStateManager.getLinkStateManager().getState().getFrameIndex(),
				cntr,
				new CfgNRGTablePanel( colorIndex, nrgStackWithParams.doOperation() ),
				mpg.getLogErrorReporter().getErrorReporter()
			);
			frame.controllerSize().configureSize(300,600, 300, 1000);
			return frame.moduleCreator();
			
		} catch (IllegalArgumentException | InitException | GetOperationFailedException | OperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}

	@Override
	public String title() {
		return "NRG Table";
	}

	@Override
	public String shortTitle() {
		return null;
	}
}
