package org.anchoranalysis.gui.videostats.modulecreator;

import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjMaskFactory;

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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class ObjMaskCollectionModuleCreator extends VideoStatsModuleCreator {

	private String fileIdentifier;
	private String name;
	private Operation<ObjMaskCollection> opObjs;
	private NRGBackground nrgBackground;
	
	private VideoStatsModuleGlobalParams mpg;
	
	public ObjMaskCollectionModuleCreator(
		String fileIdentifier,
		String name,
		Operation<ObjMaskCollection> op,
		NRGBackground nrgBackground,
		VideoStatsModuleGlobalParams mpg
	) {
		super();
		this.fileIdentifier = fileIdentifier;
		this.name = name;
		this.opObjs = op;
		this.nrgBackground = nrgBackground;
		this.mpg = mpg;
	}


	@Override
	public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
			throws VideoStatsModuleCreateException {
		try {
			ObjMaskCollection objs = opObjs.doOperation();

			OverlayCollection oc = OverlayCollectionObjMaskFactory.createWithoutColor(objs, new IDGetterIter<ObjMask>() );
			
			String frameName = String.format("%s: %s", fileIdentifier, name);
			InternalFrameStaticOverlaySelectable imageFrame = new InternalFrameStaticOverlaySelectable( frameName, false );
			ISliderState sliderState = imageFrame.init(
				oc,
				adder.getSubgroup().getDefaultModuleState().getState(),
				mpg
			);
			
			imageFrame.controllerBackgroundMenu(sliderState).add(
				mpg,
				nrgBackground.getBackgroundSet()
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
				return null;
			}
	

			@Override
			public String generateName() {
				return fileIdentifier;
			}

			@Override
			public Operation<ObjMaskCollection> getObjMaskCollection() {
				return opObjs;
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