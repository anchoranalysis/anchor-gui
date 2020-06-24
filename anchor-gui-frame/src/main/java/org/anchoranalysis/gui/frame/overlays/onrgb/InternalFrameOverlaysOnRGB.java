package org.anchoranalysis.gui.frame.overlays.onrgb;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.details.IGenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.overlays.IExtractOverlays;
import org.anchoranalysis.gui.frame.threaded.overlay.InternalFrameThreadedOverlayProvider;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.image.extent.ImageDim;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class InternalFrameOverlaysOnRGB {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(InternalFrameOverlaysOnRGB.class);
	
	private InternalFrameThreadedOverlayProvider delegate;
	
	private SingleContainer<OverlayedDisplayStack> cntr;
	private ControllerPopupMenuWithBackground controllerMenu;
	private IExtractOverlays extractOverlays;
	
	public InternalFrameOverlaysOnRGB( String title, boolean indexesAreFrames ) {
		delegate = new InternalFrameThreadedOverlayProvider(title, indexesAreFrames);
		
		cntr = new SingleContainer<>(false);
	}
	
	// Must be called before usage
	public ISliderState init(
			OverlayedDisplayStack overlayedDisplayStack,
			IDGetter<Overlay> idGetter,
			IDGetter<Mark> idColorGetter,
			boolean includeFrameAdjusting,
			DefaultModuleState initialState,
			MarkDisplaySettingsWrapper markDisplaySettingsWrapper,
			IRetrieveElements elementRetriever,
			VideoStatsModuleGlobalParams mpg
		) throws InitException {

		assert( initialState.getLinkState().getBackground() != null );
	
		//assert( coloredCfg.getCfg().getCfg().size()==coloredCfg.getCfg().getColorList().size() );
		cntr.setItem( overlayedDisplayStack , 0);
		
		FunctionWithException<Integer,OverlayedDisplayStack,GetOperationFailedException> bridge = new CfgCntrBridge(cntr);
		
		delegate.beforeInit(
			bridge,
			idGetter,
			0,
			markDisplaySettingsWrapper,
			mpg
		);
		
		BackgroundSetterLocal backgroundSetter = new BackgroundSetterLocal( delegate.getRedrawable() );
		extractOverlays = new OverlayerExtracter();
		
		// We assume all channels have the same number of slices
		ISliderState sliderState = delegate.init(
			cntr,
			includeFrameAdjusting,
			initialState,
			elementRetriever,
			mpg
		);
		
		// This must be done after init() on delegate, otherwise controllerPopupMenu() is null
		controllerMenu = new ControllerPopupMenuWithBackground(
			delegate.controllerPopupMenu(),
			backgroundSetter
		);
		
		return sliderState;
	}

	private class OverlayerExtracter implements IExtractOverlays {

		@Override
		public ColoredOverlayCollection getOverlayCollection() {
			return delegate.getOverlayRetriever().getOverlayCollection();
		}
		
		@Override
		public ImageDim getDimensions() {
			return delegate.getDimensions();
		}
	}
	
	public InternalFrameCanvas getFrameCanvas() {
		return delegate.getFrameCanvas();
	}

	public void setIndexSliderVisible(boolean visibility) {
		delegate.setIndexSliderVisible(visibility);
	}

	public IRetrieveElements getElementRetriever() {
		return delegate.getElementRetriever();
	}

	public void flush() {
		delegate.flush();
	}

	public IRedrawable getRedrawable() {
		return delegate.getRedrawable();
	}

	public boolean addAdditionalDetails(IGenerateExtraDetail arg0) {
		return delegate.addAdditionalDetails(arg0);
	}

	public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
		return controllerMenu;
	}

	public ControllerImageView controllerImageView() {
		return delegate.controllerImageView();
	}

	public IExtractOverlays extractOverlays() {
		return extractOverlays;
	}

	public ControllerAction controllerAction() {
		return delegate.controllerAction();
	}


	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return delegate.moduleCreator(sliderState);
	}


	public IIndexGettableSettable getIndexGettableSettable() {
		return delegate.getIndexGettableSettable();
	}
}
