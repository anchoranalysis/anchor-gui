package org.anchoranalysis.gui.frame.multioverlay;

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


import java.util.List;

import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.core.bridge.BridgeElementWithIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.addoverlays.AdderAddOverlaysWithStack;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.MultiInput;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> input-type
 */
public class RasterMultiCreator<T> extends VideoStatsModuleCreator {
	
	private final List<MultiInput<T>> list;
	private final String frameName;
	private final VideoStatsModuleGlobalParams moduleParamsGlobal;
	private final BridgeElementWithIndex<MultiInput<T>, OverlayedInstantState,OperationFailedException> bridge;
			
	public RasterMultiCreator(
			List<MultiInput<T>> list,
			String frameName,
			VideoStatsModuleGlobalParams moduleParamsGlobal,
			BridgeElementWithIndex<MultiInput<T>, OverlayedInstantState,OperationFailedException> bridge
		) {
		super();
		this.list = list;
		this.frameName = frameName;
		this.moduleParamsGlobal = moduleParamsGlobal;
		this.bridge = bridge;
		assert( moduleParamsGlobal.getExportPopupParams() !=null );
	}

	@Override
	public void createAndAddVideoStatsModule( IAddVideoStatsModule adder ) throws VideoStatsModuleCreateException {
		
		try {
			InternalFrameMultiOverlay<T> internalFrame = new InternalFrameMultiOverlay<>(frameName);
			SliderNRGState state = internalFrame.init(
				list,
				bridge,
				adder.getSubgroup().getDefaultModuleState(),
				moduleParamsGlobal
			);
			
			// We create a special adder
			adder = new AdderAddOverlaysWithStack( adder, moduleParamsGlobal.getThreadPool(), moduleParamsGlobal.getLogErrorReporter().errorReporter() );
			
			adder = state.addNrgStackToAdder(adder);
		
			ModuleAddUtilities.add(adder, internalFrame.moduleCreator(state.getSlider()) );
			
		} catch (InitException e) {
			throw new VideoStatsModuleCreateException(e);
		}					
	}
}