/*-
 * #%L
 * anchor-gui-frame
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
package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.frame.singleraster.InternalFrameSingleRaster;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.object.ObjectCollection;

public class RasterModuleCreator extends VideoStatsModuleCreator {
	
	private final NRGBackground nrgBackground;
	private final String fileDscr;
	private final String frameName;
	private final VideoStatsModuleGlobalParams mpg;
	
	private IVideoStatsOperationCombine combiner = new IVideoStatsOperationCombine() {

		@Override
		public Optional<Operation<Cfg,OperationFailedException>> getCfg() {
			return Optional.empty();
		}

		@Override
		public String generateName() {
			return fileDscr;
		}

		@Override
		public Optional<Operation<ObjectCollection, OperationFailedException>> getObjects() {
			return Optional.empty();
		}

		@Override
		public NRGBackground getNrgBackground() {
			return nrgBackground;
		}
		
	};
	
	public RasterModuleCreator(NRGBackground nrgBackground, String fileDscr,
			String frameName, VideoStatsModuleGlobalParams mpg) {
		super();
		this.fileDscr = fileDscr;
		this.nrgBackground = nrgBackground;
		this.frameName = frameName;
		this.mpg = mpg;
	}

	@Override
	public void createAndAddVideoStatsModule( IAddVideoStatsModule adder ) throws VideoStatsModuleCreateException {
		
		try {
			InternalFrameSingleRaster imageFrame = new InternalFrameSingleRaster(
				String.format("%s: %s", fileDscr, frameName )
			);
			ISliderState sliderState = imageFrame.init(
				nrgBackground.numFrames(),
				adder.getSubgroup().getDefaultModuleState().getState(),
				mpg
			);
			
			imageFrame.controllerBackgroundMenu().add(
				mpg,
				nrgBackground.getBackgroundSet()
			);
		
			adder.addVideoStatsModule(
				imageFrame.moduleCreator(sliderState).createVideoStatsModule( adder.getSubgroup().getDefaultModuleState().getState() )
			);
			
		} catch (InitException | OperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		}				
	}
	
	@Override
	public Optional<IVideoStatsOperationCombine> getCombiner() {
		return Optional.of(combiner);
	}
}
