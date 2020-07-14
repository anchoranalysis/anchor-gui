package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjectFactory;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;

public class ObjMaskCollectionModuleCreator extends VideoStatsModuleCreator {

	private String fileIdentifier;
	private String name;
	private Operation<ObjectCollection,OperationFailedException> opObjs;
	private NRGBackground nrgBackground;
	
	private VideoStatsModuleGlobalParams mpg;
	
	public ObjMaskCollectionModuleCreator(
		String fileIdentifier,
		String name,
		Operation<ObjectCollection,OperationFailedException> op,
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
			ObjectCollection objects = opObjs.doOperation();

			OverlayCollection oc = OverlayCollectionObjectFactory.createWithoutColor(objects, new IDGetterIter<ObjectMask>() );
			
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

		} catch (InitException | OperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}



	@Override
	public Optional<IVideoStatsOperationCombine> getCombiner() {
		return Optional.of(
			new IVideoStatsOperationCombine() {
				
				@Override
				public Optional<Operation<Cfg,OperationFailedException>> getCfg() {
					return Optional.empty();
				}
		
	
				@Override
				public String generateName() {
					return fileIdentifier;
				}
	
				@Override
				public Optional<Operation<ObjectCollection, OperationFailedException>> getObjMaskCollection() {
					return Optional.of(opObjs);
				}
	
	
				@Override
				public NRGBackground getNrgBackground() {
					return nrgBackground;
				}
			}
		);
	}
}
