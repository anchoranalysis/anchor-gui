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
package org.anchoranalysis.gui.frame.details.canvas;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Optional;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.core.index.container.BoundedRangeIncompleteDynamic;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateProvider;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateRememberStack;
import org.anchoranalysis.gui.displayupdate.IOverlayedImgStackProvider;
import org.anchoranalysis.gui.frame.canvas.ImageCanvas;
import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.display.BoundOverlayedDisplayStack;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.retrieveelements.ExportPopupParams;
import org.anchoranalysis.gui.retrieveelements.IRetrieveElements;
import org.anchoranalysis.gui.retrieveelements.InternalFrameIJPopupClickListener;
import org.anchoranalysis.gui.retrieveelements.RetrieveElements;
import org.anchoranalysis.gui.retrieveelements.RetrieveElementsImage;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class InternalFrameCanvas {

	private ImageCanvas canvas = new ImageCanvas();

	private String frameName;
	
	private WrappedSlider slider;
	
	private IOverlayedImgStackProvider stackProvider;

	private InternalFrameIJPopupClickListener popUpListener;
	
	private boolean updateToMaximumNextFlush = false;
	
	private JPanel panelTop;
	private JPanel panelBottom;
	
	private InternalFrameDelegate frame;
	
	private ErrorReporter errorReporter;
	
	private boolean useSplitPlane = true;
	
	private TitleBoundsUpdater titleBoundsUpdater;
		
	private ControllerOrder controllerOrder;
	private ControllerFrame controllerFrame;
		
	private ControllerZoom controllerZoom = new ControllerZoom() {
		
		@Override
		public void setEnforceMinimumSizeAfterGuessZoom(
				boolean enforceMinimumSizeAfterGuessZoom) {
			canvas.setEnforceMinimumSizeAfterGuessZoom(enforceMinimumSizeAfterGuessZoom);
		}

		@Override
		public void setDefaultZoomSuggestor(
				DefaultZoomSuggestor defaultZoomSuggestor) {
			canvas.setDefaultZoomSuggestor(defaultZoomSuggestor);
		}
	};
	
	private ControllerKeyboard controllerKeyboard = new ControllerKeyboard() {
		
		@Override
		public InputMap getInputMap() {
			return canvas.getInputMap();
		}

		@Override
		public ActionMap getActionMap() {
			return canvas.getActionMap();
		}
	};
	
	private ControllerMouse controllerMouse = new ControllerMouse() {
		
		@Override
		public void addMouseListener(MouseListener l, boolean absCoord) {
			canvas.addMouseListener(l,absCoord);
		}
	};
		
	private ControllerAction controllerAction;
	
	private ControllerImageView controllerImageView;
	
	public InternalFrameCanvas( String frameName ) {
		this.frame = new InternalFrameDelegate( frameName, true, true, true, true );
		this.frameName = frameName;
		this.frame.setTitle( frameName );
		//this.frame.setMinimumSize( new Dimension(150,70) );
		
		this.panelTop = new JPanel();
		this.panelTop.setLayout( new BorderLayout() );
		panelTop.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		
		// We add these here, as they will be referenced in setAsBottomComponent() which should be called before init()
		this.panelBottom = new JPanel();
		this.panelBottom.setLayout( new BorderLayout() );
		panelBottom.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		
		//this.canvas.add
		

		frame.addInternalFrameListener( new DisposeCanvasWhenFrameIsClosed(canvas) );
		
		ControllerSize controllerSize = new SizeController();
		
		controllerImageView = new ControllerImageView(
			controllerSize,
			controllerZoom
		);
		
		controllerOrder = new ControllerOrder() {

			public void setAsTopComponent( JComponent component ) {
				setAsPanelComponent(panelTop, component);
			}
			
			// SHOULD BE CALLED BEFORE INIT
			public void setAsBottomComponent( JComponent component ) {
				setAsPanelComponent(panelBottom, component);
			}
		};
		
		controllerFrame = new FrameController();
		
		controllerAction =new ControllerAction(
			controllerOrder,
			controllerFrame,
			controllerMouse,
			controllerKeyboard
		);
	}
		
	public ISliderState init(
		BoundedRangeIncompleteDynamic indexBounds,
		IIndexGettableSettable indexCntr,
		IDisplayUpdateRememberStack stackProvider,
		InitialSliderState initialState,
		IRetrieveElements elementRetriever,
		final VideoStatsModuleGlobalParams mpg
	) throws InitException {

		this.stackProvider = stackProvider;
		this.errorReporter = mpg.getLogger().errorReporter();
		
		
		
		
		
		
		BoundOverlayedDisplayStack initialStack;
		try {
			initialStack = this.stackProvider.getCurrentDisplayStack();
		} catch (GetOperationFailedException e1) {
			throw new InitException(e1);
		}
		ChnlSliceRange sliceBounds = new ChnlSliceRange( initialStack.getDimensions() );
		
		// Responsible for all the stack conversion
		
		// Create slider panel
		this.slider = new WrappedSlider( sliceBounds, indexBounds, initialState, indexCntr );
		
		
		// Create ImageJ Canvas
		canvas.init( stackProvider, errorReporter );
		

		
		JPanel panel = new JPanel();
		{
			panel.setLayout( new BorderLayout() );
			panel.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
			panel.add( canvas.getPanel() );
	
			panel.setMinimumSize( canvas.getMinimumSize() );
			panel.setPreferredSize( canvas.getPrefferedSize() );
		}
		
		this.panelBottom.add( slider.getComponent(), BorderLayout.SOUTH );


		
		
		frame.add( panelTop, BorderLayout.NORTH );
		
		addPanelMaybeSplit( panel );
		
		frame.setVisible(true);
		
		addTitleBoundsUpdater(stackProvider, indexCntr);
				
		canvas.addMouseWheelListener( new MouseWheelListenerSlices(slider.getSlider() ) );
		
		slider.configure(canvas);
		
		controllerMouse.addMouseListener( new FocusRequester(canvas), false );
		
		addPopup( mpg.getExportPopupParams(), elementRetriever );
				
		titleBoundsUpdater.updateTitle();
		
		return slider;
	}
	
	public synchronized void flush() {
		
		if (updateToMaximumNextFlush==true) {
			
			slider.setIndexToMaximum();
			updateToMaximumNextFlush = false;
		}
	}

	@Override
	public String toString() {
		return canvas.toString();
	}
			
	public IRetrieveElements getElementRetriever() {
		return new RetrieveElementsLocal();
	}
	
	public void setIndexSliderVisible( boolean visibility ) {
		slider.setIndexSliderVisible(visibility);
		titleBoundsUpdater.updateTitle();
	}


	public ImageDimensions getDimensions() {
		return canvas.getDimensions();
	}

	public void addMouseMotionListener(MouseMotionListener arg0, boolean absCoord) {
		canvas.addMouseMotionListener(arg0, absCoord);
	}

	public ZoomScale getZoomScale() {
		return canvas.getZoomScale();
	}

	public ImageResolution getRes() {
		return canvas.getRes();
	}

	public boolean canvasContainsAbs(int x, int y) {
		return canvas.canvasContainsAbs(x, y);
	}


	public String intensityStrAtAbs(int x, int y) {
		return canvas.intensityStrAtAbs(x, y);
	}
	
	// empty() means it cannot be determined
	public Optional<VoxelDataType> associatedDataType() {
		return canvas.associatedDataType();
	}


	public int hashCode() {
		return canvas.hashCode();
	}


	public void init(IDisplayUpdateProvider imageProvider,
			ErrorReporter errorReporter) throws InitException {
		canvas.init(imageProvider, errorReporter);
	}


	public boolean equals(Object obj) {
		return canvas.equals(obj);
	}


	public void resizeEventFromFrame(ComponentEvent e) {
		canvas.resizeEventFromFrame(e);
	}



	public void zoomIn() {
		canvas.zoomIn( null );
	}


	public void zoomOut() {
		canvas.zoomOut( null );
	}

	public ControllerImageView controllerImageView() {
		return controllerImageView;
	}

	public ControllerPopupMenu controllerPopupMenu() {
		assert(popUpListener!=null);
		return popUpListener.controllerPopupMenu();
	}

	private class RetrieveElementsLocal implements IRetrieveElements {
		
		@Override
		public RetrieveElements retrieveElements() {
			
			RetrieveElementsImage rei = new RetrieveElementsImage();

			try {
				DisplayStack stack = stackProvider.getCurrentDisplayStack().extractFullyOverlayed(); 
				rei.setStack( stack );
				rei.setSlice( stack.extractSlice( canvas.getSlice() ) );
			} catch (GetOperationFailedException | CreateException | OperationFailedException e) {
				errorReporter.recordError(InternalFrameCanvas.class, e);
				rei.setStack( null );
				rei.setSlice( null );
			}			
			return rei;
		}
	}
	
	private class FrameController implements ControllerFrame {
		
		@Override
		public void setUseSplitPlane(boolean use) {
			useSplitPlane = use;
		}

		@Override
		public void addInternalFrameListener(InternalFrameListener l) {
			frame.addInternalFrameListener(l);
		}

		@Override
		public void setDefaultCloseOperation(int operation) {
			frame.setDefaultCloseOperation(operation);
		}
				
		@Override
		public JInternalFrame getFrame() {
			return frame.getFrame();
		}
	}

	private class SizeController extends ControllerSize {
	
		@Override
		protected void setMinimumSize(Dimension minimumSize) {
			frame.getContentPane().setMinimumSize(minimumSize);
		}
	
	
		@Override
		protected void setPreferredSize(Dimension preferredSize) {
			frame.getContentPane().setPreferredSize(preferredSize);
		}
	};
	
	private class DisposeCanvasWhenFrameIsClosed extends InternalFrameAdapter {

		private ImageCanvas canvas;
				
		public DisposeCanvasWhenFrameIsClosed(ImageCanvas canvas) {
			super();
			this.canvas = canvas;
		}

		@Override
		public void internalFrameClosed(InternalFrameEvent e) {
			slider.dispose();
			slider = null;
			stackProvider = null;
			panelTop.removeAll();
			panelBottom.removeAll();
			panelTop = null;
			panelBottom = null;
			popUpListener = null;
			canvas.dispose();
			canvas = null;
			frame.getContentPane().removeAll();
			frame.dispose();
			frame = null;
		}
		
	}

	private void setAsPanelComponent( JPanel panel, JComponent component ) {
		if (panel.getComponentCount()>0) {
			panel.removeAll();
		}
		panel.add( component, BorderLayout.CENTER );
	}
	

	private void addPanelMaybeSplit( JPanel panel ) {
		// We only use a split pane if there is more in the panelBottom than the index slider
		if (this.panelBottom.getComponentCount()>1 && useSplitPlane) {
			
			JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, panelBottom);
			splitPane.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
			 //splitPane.setResizeWeight(0.5);
			frame.add( splitPane, BorderLayout.CENTER );
			
		} else {
			frame.add( panel, BorderLayout.CENTER );
			frame.add( panelBottom, BorderLayout.SOUTH );
		}		
	}
		
	private void addTitleBoundsUpdater( IDisplayUpdateRememberStack stackProvider, IIndexGettableSettable indexCntr ) {
		this.titleBoundsUpdater = new TitleBoundsUpdater(
			errorReporter,
			indexCntr,
			stackProvider,
			slider.getSlider(),
			frame,
			frameName
		);
		stackProvider.addChangeListener( titleBoundsUpdater );
	}
	
	
	private void addPopup( ExportPopupParams exportPopupParams, IRetrieveElements elementRetriever ) {
		popUpListener = new InternalFrameIJPopupClickListener(
			exportPopupParams,
			elementRetriever,
			errorReporter
		);
		controllerMouse.addMouseListener( popUpListener, true );
	}

	public ControllerAction controllerAction() {
		return controllerAction;
	}
}
