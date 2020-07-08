package org.anchoranalysis.gui.frame.multioverlay.instantstate;

import java.util.Optional;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.OverlayedInstantState;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;

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




import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.PropertyValueReceivableFromIndicesSelection;
import org.anchoranalysis.gui.frame.details.IGenerateExtraDetail;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.frame.display.overlay.IGetOverlayCollection;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.indices.DualIndicesSelection;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsList;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsOverlayCollection;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.ColoredOverlayedInstantState;
import org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay.MarkDisplaySettingsWrapper;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// An internal frame, that converts a configuration to RGB
public class InternalFrameOverlayedInstantStateToRGBSelectable {

	static Log log = LogFactory.getLog(InternalFrameOverlayedInstantStateToRGBSelectable.class);
	
	// The current selection within the frame
	private DualIndicesSelection selectionIndices = new DualIndicesSelection();
	private ClickAdapter markClickAdapter;
	
	private InternalFrameOverlayedInstantStateToRGB delegate;
	
	private CurrentlySelectedMarks currentlySelectedMarksGetter = new CurrentlySelectedMarks();
	
	private boolean sendReceiveIndices = false;
	
	// Returns a Cfg representing the currently selected marks
	private class CurrentlySelectedMarks implements IGetOverlayCollection {
		
		@Override
		public ColoredOverlayCollection getOverlayCollection() {
			return delegate.getOverlayRetriever().getOverlayCollection().createSubsetFromIDs( selectionIndices.getCurrentSelection() );
		}
		
	};
	
	public InternalFrameOverlayedInstantStateToRGBSelectable( String title, boolean indexesAreFrames, boolean sendReceiveIndices ) {
		delegate = new InternalFrameOverlayedInstantStateToRGB(title,indexesAreFrames);
		this.sendReceiveIndices = sendReceiveIndices;
	}
	
	
	// Must be called before usage
	public ISliderState init(
			BoundedIndexContainer<OverlayedInstantState> cfgCntr,
			ColorIndex colorIndex,
			IDGetter<Overlay> idGetter,
			IDGetter<Overlay> idColorGetter,
			boolean includeFrameAdjusting,
			DefaultModuleState initialState,
			VideoStatsModuleGlobalParams mpg
		) throws InitException {
		
		// WE MUST SET THIS TO THE CORRECT initial state, as the frameIJ will not trigger events on its default state, to correct itself
		this.selectionIndices.setCurrentSelection( initialState.getLinkState().getObjectIDs() );

		// We create a wrapper that conditions the MarkDisplaySettings on the current selection 
		MarkDisplaySettingsWrapper markDisplaySettingsWrapper =	new MarkDisplaySettingsWrapper(
			initialState.getMarkDisplaySettings().duplicate(),
			(ObjectWithProperties mask, RGBStack stack, int id) -> selectionIndices.getCurrentSelection().contains(id)
		);
		
		
		BoundedIndexContainer<ColoredOverlayedInstantState> cfgCntrColored
			= new BoundedIndexContainerBridgeWithoutIndex<>(
				cfgCntr,
				new AddColorBridge( colorIndex, idColorGetter )
			);
				
		ISliderState sliderState = delegate.init(
			cfgCntrColored,
			idGetter,
			includeFrameAdjusting,
			initialState,
			markDisplaySettingsWrapper,
			createElementRetriever(),
			mpg
		);
		
		IGenerateExtraDetail cfgSizeDetail = new IGenerateExtraDetail() {
			
			@Override
			public String genStr(int index) {
				OverlayRetriever or = delegate.getOverlayRetriever();
				return String.format("cfgSize=%s", or.getOverlayCollection()!=null ? or.getOverlayCollection().size() : -1 );
			}
		};
		
		delegate.addAdditionalDetails(cfgSizeDetail);
		
		// When a new object is selected, then we need to redraw (partially)
		new PropertyValueReceivableFromIndicesSelection(selectionIndices.getCurrentSelection()).addPropertyValueChangeListener(
			new RedrawFromCfgGetter(
				currentlySelectedMarksGetter,
				delegate.getRedrawable(),
				mpg.getLogErrorReporter()
			)
		);
		
		markClickAdapter = new ClickAdapter(
			selectionIndices,
			sliderState,
			delegate.getOverlayRetriever()
		);
		delegate.controllerAction().mouse().addMouseListener( markClickAdapter, false );
		
		return sliderState;
	}
	
	private IRetrieveElements createElementRetriever() {
		return new IRetrieveElements() {
			
			@Override
			public RetrieveElements retrieveElements() {
				
				RetrieveElementsList rel = new RetrieveElementsList();
				rel.add( delegate.getElementRetriever().retrieveElements() );
				
				RetrieveElementsOverlayCollection rempp = new RetrieveElementsOverlayCollection();
				rempp.setCurrentSelectedObjects( currentlySelectedMarksGetter.getOverlayCollection().getOverlayCollection() );
				rempp.setCurrentObjects( delegate.getOverlayRetriever().getOverlayCollection().getOverlayCollection() );
				
				rel.add( rempp );
				return rel;
			}
		};
		
	}
	
	private void addSendReceiveIndicesToModule( VideoStatsModule module ) {
		
		LinkModules link = new LinkModules(module);
		link.getMarkIndices().add(
			Optional.of(
				new PropertyValueReceivableFromIndicesSelection(selectionIndices.getLastExplicitSelection())
			),
			Optional.of(
				(value, adjusting) -> {
					// If the ids are the same as our current selection, we don't need to change
					// anything
					if (!selectionIndices.setCurrentSelection(value.getArr())) {
						return;
					}
				}
			)
		);
	}
	
	public IModuleCreatorDefaultState moduleCreator(ISliderState sliderState) {
		return defaultFrameState-> {
						
			VideoStatsModule module = delegate.moduleCreator(sliderState).createVideoStatsModule(defaultFrameState);
			
			if (sendReceiveIndices)	{
				addSendReceiveIndicesToModule(module);
			}
			
			LinkModules link = new LinkModules(module);
			link.getOverlays().add(
				Optional.of(
					markClickAdapter.createSelectOverlayCollectionReceivable()
				)
			);
			return module;
		};
	}

	public void setIndexSliderVisible(boolean visibility) {
		delegate.setIndexSliderVisible(visibility);
	}

	public void flush() {
		delegate.flush();
	}

	public IPropertyValueReceivable<OverlayCollection> createSelectCfgReceivable() {
		return markClickAdapter.createSelectOverlayCollectionReceivable();
	}

	public InternalFrameCanvas getFrameCanvas() {
		return delegate.getFrameCanvas();
	}

	public IRetrieveElements getElementRetriever() {
		return delegate.getElementRetriever();
	}

	public boolean addAdditionalDetails(IGenerateExtraDetail arg0) {
		return delegate.addAdditionalDetails(arg0);
	}

	public ControllerPopupMenuWithBackground controllerBackgroundMenu( ISliderState sliderState ) {
		return delegate.controllerBackgroundMenu(sliderState);
	}
}
