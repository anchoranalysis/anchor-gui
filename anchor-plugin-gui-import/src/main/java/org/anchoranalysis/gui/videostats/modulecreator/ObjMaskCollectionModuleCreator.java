package org.anchoranalysis.gui.videostats.modulecreator;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjMaskFactory;
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
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

public class ObjMaskCollectionModuleCreator extends VideoStatsModuleCreator {

	private String fileIdentifier;
	private String name;
	private Operation<ObjMaskCollection,OperationFailedException> opObjs;
	private NRGBackground nrgBackground;
	
	private VideoStatsModuleGlobalParams mpg;
	
	public ObjMaskCollectionModuleCreator(
		String fileIdentifier,
		String name,
		Operation<ObjMaskCollection,OperationFailedException> op,
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

		} catch (InitException | OperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}



	@Override
	public IVideoStatsOperationCombine getCombiner() {
		return new IVideoStatsOperationCombine() {
			
			@Override
			public Operation<Cfg,OperationFailedException> getCfg() {
				return null;
			}
	

			@Override
			public String generateName() {
				return fileIdentifier;
			}

			@Override
			public Operation<ObjMaskCollection,OperationFailedException> getObjMaskCollection() {
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