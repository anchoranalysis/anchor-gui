package org.anchoranalysis.gui.videostats.internalframe;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetter;

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


import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.frame.multioverlay.instantstate.InternalFrameOverlayedInstantStateToRGBSelectable;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;

import ch.ethz.biol.cell.gui.overlay.IDGetterOverlayID;
import ch.ethz.biol.cell.gui.overlay.Overlay;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.mpp.instantstate.OverlayedInstantState;

public class InternalFrameStaticOverlaySelectable {

	private InternalFrameOverlayedInstantStateToRGBSelectable delegate;
	
	public InternalFrameStaticOverlaySelectable( String title, boolean sendReceiveIndices ) {
		this.delegate = new InternalFrameOverlayedInstantStateToRGBSelectable(title,false, sendReceiveIndices);
	}
	
	public ISliderState init(
		OverlayCollection oc,
		DefaultModuleState defaultState,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		
		OverlayedInstantState cis = new OverlayedInstantState(0, oc);
		
		SingleContainer<OverlayedInstantState> cfgCntr = new SingleContainer<>(false);
		cfgCntr.setItem(cis, cis.getIndex());
		
		IDGetter<Overlay> idGetter = new IDGetterOverlayID(); 
		
		ISliderState sliderState = this.delegate.init(
			cfgCntr,
			mpg.getDefaultColorIndexForMarks(),
			idGetter,
			idGetter,
			false,
			defaultState,
			mpg
		);
		
		this.delegate.setIndexSliderVisible(false);
		
		return sliderState;
	}

	public ControllerPopupMenuWithBackground controllerBackgroundMenu( ISliderState sliderState ) {
		return delegate.controllerBackgroundMenu(sliderState);
	}

	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return delegate.moduleCreator(sliderState);
	}
}
