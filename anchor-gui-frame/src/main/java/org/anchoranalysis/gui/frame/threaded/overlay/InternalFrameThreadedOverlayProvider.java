package org.anchoranalysis.gui.frame.threaded.overlay;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.index.container.IBoundedRangeIncompleteDynamic;
import org.anchoranalysis.gui.displayupdate.OverlayedDisplayStack;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.IGenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.IRedrawable;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.frame.threaded.stack.InternalFrameThreadedProvider;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.extent.ImageDimensions;

public class InternalFrameThreadedOverlayProvider {

	private InternalFrameThreadedProvider delegate;
	
	private MarkDisplaySettingsWrapper markDisplaySettingsWrapper;
	private ThreadedOverlayUpdateProducer threadedImageStackProvider;
	
	public InternalFrameThreadedOverlayProvider( String title, boolean indexesAreFrames ) {
		delegate = new InternalFrameThreadedProvider(title, indexesAreFrames);
	}

	public void beforeInit(
		FunctionWithException<Integer, OverlayedDisplayStack,GetOperationFailedException> bridge,
		IDGetter<Overlay> idGetter,
		int defaultIndex,
		MarkDisplaySettingsWrapper markDisplaySettingsWrapper,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		
		this.markDisplaySettingsWrapper = markDisplaySettingsWrapper;
				
		threadedImageStackProvider = new ThreadedOverlayUpdateProducer(	idGetter, mpg.getLogErrorReporter()	);
		threadedImageStackProvider.init(
			bridge,
			markDisplaySettingsWrapper,
			defaultIndex,
			mpg.getThreadPool(),
			mpg.getLogErrorReporter().getErrorReporter()
		);
	}

	public ISliderState init(
		IBoundedRangeIncompleteDynamic indexBounds,
		boolean includeFrameAdjusting,
		DefaultModuleState initialState,
		IRetrieveElements elementRetriever,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		return delegate.init(
			threadedImageStackProvider,
			indexBounds,
			includeFrameAdjusting,
			initialState,
			elementRetriever,
			mpg
		);
	}

	public int defaultIndex(DefaultModuleState initialState) {
		return delegate.defaultIndex(initialState);
	}

	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return defaultFrameState -> {
			VideoStatsModule module = delegate.moduleCreator(sliderState).createVideoStatsModule(defaultFrameState);
			module.setChangeMarkDisplaySendable( markDisplaySettingsWrapper );
			return module;
		};
	}

	public ControllerPopupMenu controllerPopupMenu() {
		return delegate.controllerPopupMenu();
	}

	public InternalFrameCanvas getFrameCanvas() {
		return delegate.getFrameCanvas();
	}

	public IRetrieveElements getElementRetriever() {
		return delegate.getElementRetriever();
	}

	public ControllerAction controllerAction() {
		return delegate.controllerAction();
	}

	public void setIndexSliderVisible(boolean visibility) {
		delegate.setIndexSliderVisible(visibility);
	}

	public boolean addAdditionalDetails(IGenerateExtraDetail arg0) {
		return delegate.addAdditionalDetails(arg0);
	}

	public ControllerImageView controllerImageView() {
		return delegate.controllerImageView();
	}

	public void flush() {
		delegate.flush();
	}

	public ImageDimensions getDimensions() {
		return delegate.getDimensions();
	}

	public IIndexGettableSettable getIndexGettableSettable() {
		return delegate.getIndexGettableSettable();
	}
	
	public IRedrawable getRedrawable() {
		return threadedImageStackProvider;
	}
	
	public OverlayRetriever getOverlayRetriever() {
		return threadedImageStackProvider.getOverlayRetriever();
	}
}
