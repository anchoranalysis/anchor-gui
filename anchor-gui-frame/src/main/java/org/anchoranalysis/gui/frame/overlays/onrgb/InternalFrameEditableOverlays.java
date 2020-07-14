package org.anchoranalysis.gui.frame.overlays.onrgb;

import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.index.GetOperationFailedException;

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


import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.overlays.ExtractOverlays;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsList;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsOverlayCollection;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.IColoredCfgUpdater;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;

public class InternalFrameEditableOverlays implements IColoredCfgUpdater {

	private InternalFrameOverlaysOnRGB delegate;
	
	public InternalFrameEditableOverlays( String title ) {
		this.delegate = new InternalFrameOverlaysOnRGB(title,false);
	}
	
	public ISliderState init(
		DefaultModuleState defaultState,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
	
		// We define our mark display settings without making them conditional on anything
		MarkDisplaySettingsWrapper markDisplaySettings = new MarkDisplaySettingsWrapper( defaultState.getMarkDisplaySettings().duplicate() );
		
		try {
			ISliderState sliderState = this.delegate.init(
				new OverlayedDisplayStack(
					new ColoredOverlayCollection(),
					defaultState.getLinkState().getBackground().apply(0)
				),
				new IDGetterOverlayID(),
				false,
				defaultState,
				markDisplaySettings,
				createElementRetriever(),
				mpg
			);
			this.delegate.setIndexSliderVisible(false);	
			return sliderState;
			
		} catch (GetOperationFailedException e) {
			throw new InitException(e);
		}
	}
	
	
	// We send an update to the Cfg
	@Override
	public synchronized void applyUpdate( OverlayedDisplayStackUpdate update ) {
		this.delegate.getRedrawable().applyRedrawUpdate(update);
	}
	
	private static class RetrieveElementsLocal implements IRetrieveElements {
		
		private InternalFrameOverlaysOnRGB internalFrame;
		
		public RetrieveElementsLocal(
				InternalFrameOverlaysOnRGB internalFrame) {
			super();
			this.internalFrame = internalFrame;
		}

		@Override
		public RetrieveElements retrieveElements() {
			
			RetrieveElementsList rel = new RetrieveElementsList();
			rel.add( internalFrame.getElementRetriever().retrieveElements() );
			
			RetrieveElementsOverlayCollection rempp = new RetrieveElementsOverlayCollection();
			rempp.setCurrentObjects( internalFrame.extractOverlays().getOverlays().getOverlays() );
			
			rel.add( rempp );
			return rel;
		}
	}
	
	private IRetrieveElements createElementRetriever() {
		return new RetrieveElementsLocal(delegate);
	}

	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return delegate.moduleCreator(sliderState);
	}

	public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
		return delegate.controllerBackgroundMenu();
	}

	public ControllerImageView controllerImageView() {
		return delegate.controllerImageView();
	}

	public ExtractOverlays extractOverlays() {
		return delegate.extractOverlays();
	}

	public ControllerAction controllerAction() {
		return delegate.controllerAction();
	}

}
