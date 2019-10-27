package org.anchoranalysis.gui.videostats.modulecreator;

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

import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class CfgModuleCreator extends VideoStatsModuleCreator {

	private String fileIdentifier;
	private String name;
	private Operation<Cfg> opCfg;
	private NRGBackground nrgBackground;
	private VideoStatsModuleGlobalParams mpg;
	
	
	
	public CfgModuleCreator(
		String fileIdentifier,
		String name,
		Operation<Cfg> opCfg,
		NRGBackground nrgBackground,
		VideoStatsModuleGlobalParams mpg
	) {
		super();
		this.fileIdentifier = fileIdentifier;
		this.name = name;
		this.opCfg = opCfg;
		this.nrgBackground = nrgBackground;
		this.mpg = mpg;
	}



	@Override
	public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
			throws VideoStatsModuleCreateException {
		
		try {
			Cfg cfg = opCfg.doOperation();
			OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(cfg);
			
			String frameName = String.format("%s: %s", fileIdentifier, name);
			InternalFrameStaticOverlaySelectable imageFrame = new InternalFrameStaticOverlaySelectable( frameName, true );
			ISliderState sliderState = imageFrame.init(
				oc,
				adder.getSubgroup().getDefaultModuleState().getState(),
				mpg
			);
			
			imageFrame.controllerBackgroundMenu(sliderState).addDefinition(
				mpg,
				new ChangeableBackgroundDefinitionSimple( nrgBackground.getBackgroundSet() )
			);
			ModuleAddUtilities.add(adder, imageFrame.moduleCreator(sliderState) );
			
		} catch (InitException e) {
			throw new VideoStatsModuleCreateException(e);
		} catch (ExecuteException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}



	@Override
	public IVideoStatsOperationCombine getCombiner() {
		return new IVideoStatsOperationCombine() {
			
			@Override
			public Operation<Cfg> getCfg() {
				return opCfg;
			}
	
			@Override
			public String generateName() {
				return fileIdentifier;
			}

			@Override
			public Operation<ObjMaskCollection> getObjMaskCollection() {
				return null;
			}

			@Override
			public NRGBackground getNrgBackground() {
				return nrgBackground;
			}
		};
	}



	@Override
	public boolean canCombineOperations() {
		return true;
	}
	
}