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
		public Optional<Operation<ObjectCollection, OperationFailedException>> getObjMaskCollection() {
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
