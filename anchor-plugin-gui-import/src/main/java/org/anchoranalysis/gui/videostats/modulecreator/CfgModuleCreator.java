package org.anchoranalysis.gui.videostats.modulecreator;

import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.cache.Operation;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class CfgModuleCreator extends VideoStatsModuleCreator {

	private String fileIdentifier;
	private String name;
	private Operation<Cfg> opCfg;
	private NRGBackground nrgBackground;
	private VideoStatsModuleGlobalParams mpg;
	private MarkDisplaySettings markDisplaySettings;
	
	public CfgModuleCreator(
		String fileIdentifier,
		String name,
		Operation<Cfg> opCfg,
		NRGBackground nrgBackground,
		VideoStatsModuleGlobalParams mpg,
		MarkDisplaySettings markDisplaySettings
	) {
		super();
		this.fileIdentifier = fileIdentifier;
		this.name = name;
		this.opCfg = opCfg;
		this.nrgBackground = nrgBackground;
		this.mpg = mpg;
		this.markDisplaySettings = markDisplaySettings;
	}

	@Override
	public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
			throws VideoStatsModuleCreateException {
		
		try {
			Cfg cfg = opCfg.doOperation();
			OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
				cfg,
				markDisplaySettings.regionMembership()
			);
			
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