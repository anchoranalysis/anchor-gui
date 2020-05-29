package org.anchoranalysis.gui.frame.canvas;

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


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Optional;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateProvider;
import org.anchoranalysis.gui.frame.canvas.zoom.DefaultZoomSuggestor;
import org.anchoranalysis.gui.frame.canvas.zoom.ZoomScale;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

public class ImageCanvas {
	
	private ImageCanvasSwing imageCanvas = null;
	
	private IDisplayUpdateProvider imageProvider;
	
	private int slice = 0;
	
	private ErrorReporter errorReporter;

	// How we track what's currently displayed
	private DisplayStackViewportZoomed displayStackViewport;
	
	private EventListenerList eventList = new EventListenerList();
	
	private ExtntScrollBars extentScrollbars;
	
	private JPanel panel;
	
	private DefaultZoomSuggestor defaultZoomSuggestor = new DefaultZoomSuggestor(800,600);
	
	private boolean enforceMinimumSizeAfterGuessZoom = false;

	private static Dimension minDim = new Dimension(200,200);
	
	public ImageCanvas() {
		super();
		
		this.panel = new JPanel();
		this.panel.setLayout( new BorderLayout() );
		this.panel.setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0) );
		this.panel.setMinimumSize( minDim );
	}

	
	private class UpdateImageListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			
			try {
				// We look for any changes that might have happened since last time
				applyPendingAndUpdate( displayStackViewport.getZoomScale() );
								
			} catch (OperationFailedException e1) {
				errorReporter.recordError(ImageCanvas.class, e1);
			}
		}
	}
	
	
	private class UpdateViewportOnlyListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			try {
				updateViewportOnly();
			} catch (OperationFailedException e1) {
				errorReporter.recordError(ImageCanvas.class, e1);
			}
		}
	}
	
	
	public void init( IDisplayUpdateProvider imageProvider, final ErrorReporter errorReporter ) throws InitException {
		
		this.displayStackViewport = new DisplayStackViewportZoomed();
		
		assert(errorReporter!=null);
		this.errorReporter = errorReporter;
		this.imageProvider = imageProvider;
		this.imageProvider.addChangeListener( new UpdateImageListener() );
		
		
		
		this.imageCanvas = new ImageCanvasSwing();
		
		MouseEventCreator mouseEventCreator = new MouseEventCreator(imageCanvas,displayStackViewport);
		this.imageCanvas.addMouseListener( new MouseListenerRel(mouseEventCreator,eventList) );
		this.imageCanvas.addMouseMotionListener( new MouseMotionListenerRel(mouseEventCreator,eventList) );
		
		this.imageCanvas.addMouseWheelListener( new MouseWheelListenerZoom(this) );
		
		//createDisplayStackCurrentlyShown();

		MouseMotionListenerDragView mmlDragView = new MouseMotionListenerDragView(this,displayStackViewport,errorReporter);
		this.imageCanvas.addMouseListener( mmlDragView );
		this.imageCanvas.addMouseMotionListener( mmlDragView );
		//eventList.add( MouseListener.class, mmlDragView );
		//eventList.add( MouseMotionListener.class, mmlDragView );
		
		this.extentScrollbars = new ExtntScrollBars();
		this.extentScrollbars.addChangeListener( new UpdateViewportOnlyListener() );
		
		
		this.panel.add( imageCanvas, BorderLayout.CENTER );
		this.panel.add( extentScrollbars.getScrollVer(), BorderLayout.EAST );
		this.panel.add( extentScrollbars.getScrollHor(), BorderLayout.SOUTH );
			
		
		
		//this.panel.setMinimumSize( new Dimension(700,700) );
		try {
			// We look for any changes that might have happened since last time
			// Selecting a zoom level of null, means it's automatically selected
				applyPendingAndUpdate( null );

				
				
								
				if (enforceMinimumSizeAfterGuessZoom) {
					Extent e = displayStackViewport.getBBox().extent();
					panel.setMinimumSize( new Dimension(e.getX(),e.getY()) );
				}
				
		} catch (OperationFailedException e) {
			throw new InitException(e);
		}
		
		addComponentListener( new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				resizeEventFromFrame(e);
			}
		});
	}
	
	public void resizeEventFromFrame( ComponentEvent e ) {
		try {
			setScrollBarExtnt();
			
			// Keeps the current zoom level
			updateImage(displayStackViewport.getZoomScale());
		} catch (OperationFailedException e1) {
			errorReporter.recordError( ImageCanvas.class, e1);
		}
	}

	
	// if absCoord out listener returns the absolute coordinates
	public void addMouseMotionListener(MouseMotionListener l, boolean absCoord ) {
		// We use our own listeners, which instead use co-ordinates which take account of the zoom factor
		if (absCoord) {
			imageCanvas.addMouseMotionListener(l);
		} else {
			eventList.add( MouseMotionListener.class, l);
		}
	}
	
	public void addMouseListener(MouseListener l, boolean absCoord) {
		// We use our own listeners, which instead use co-ordinates which take account of the zoom factor
		if (absCoord) {
			imageCanvas.addMouseListener(l);	
		} else {
			eventList.add( MouseListener.class, l);
		}
	}

	// ZoomesIn (with an optional mouse-point if the zoom is centered around a new point)
	public void zoomIn( Point2i mousePoint ) {
		ZoomScale zoomScaleNew = displayStackViewport.getZoomScale().zoomIn();
		try {
			if (zoomScaleNew!=null) {
				updateViewportOnly( zoomScaleNew, mousePoint );
			}
		} catch (OperationFailedException e) {
			errorReporter.recordError(ImageCanvas.class, e);
		}
	}
	
	public void zoomOut( Point2i mousePoint  ) {
		ZoomScale zoomScaleNew = displayStackViewport.getZoomScale().zoomOut();
		try {
			if (zoomScaleNew!=null) {
				updateViewportOnly( zoomScaleNew, mousePoint );
			}
		} catch (OperationFailedException e) {
			errorReporter.recordError(ImageCanvas.class, e);
		}
	}
		

	public void shiftViewport( Point2i shift ) throws OperationFailedException {
			
		BoundingBox shiftedBox = displayStackViewport.createBoxForShiftedView(shift, imageCanvas.createExtnt() );
		
		assert( displayStackViewport.createDimensionsEntireScaled().contains(shiftedBox) );
		
		extentScrollbars.setValue( new Point2i(shiftedBox.getCrnrMin().getX(), shiftedBox.getCrnrMin().getY()) );
		
		updateStackViewportForImageExtnt( shiftedBox.extent() );
	}
	

	public void changeSlice(int z) {
		this.slice = z;

		//System.out.printf("Changing slice to %d%n", z);
		try {
			//applyPending();
			
			Extent e = calcExtntToRetrieveScaled( displayStackViewport.createDimensionsEntireScaled() );
			updateStackViewportForImageExtnt(e);
			
		} catch (OperationFailedException e) {
			errorReporter.recordError(ImageCanvas.class, e);
		}
	}

	// Updates the image with a given zoomScale (null means it guesses)
	private void updateImage( ZoomScale zoomScaleNew ) throws OperationFailedException {
		
		// If we don't have a zoom scale we try to pick an intelligent default
		if (zoomScaleNew==null) {
			zoomScaleNew = defaultZoomSuggestor.suggestDefaultZoomFor( displayStackViewport.getDisplayStackEntireImage().getDimensions() );
		}
		
		zoomScaleNew.establishBounds( displayStackViewport.getDimensionsEntire() );
		updateViewportOnly( zoomScaleNew, null );
	}
	
	private void updateViewportOnly( ZoomScale zoomScaleNew, Point2i mousePoint ) throws OperationFailedException {
		
		
		Point2i scrollVal = displayStackViewport.removeScale( extentScrollbars.value() );
		ZoomScale zoomScaleOld = displayStackViewport.getZoomScale();
		
		displayStackViewport.setZoomScale(zoomScaleNew);
	
		{
			// We interpolate a region from the image
			Extent extentNew = calcExtntToRetrieveScaled( displayStackViewport.createDimensionsEntireScaled() );
	
			// We update the scrollbars as well
			updateScollBarsWithNewExtnt( extentNew, zoomScaleOld, zoomScaleNew, scrollVal, mousePoint );
				
			updateStackViewportForImageExtnt(extentNew );
		}
	
	}


	private void updateScollBarsWithNewExtnt( Extent extentNew, ZoomScale zoomScaleOld, ZoomScale zoomScaleNew, Point2i scrollVal, Point2i mousePoint ) {
		
		Extent canvasExtntOld = imageCanvas.createExtnt();
		
		assert( displayStackViewport.createDimensionsEntireScaled().contains( new BoundingBox(extentNew)) );
		
		Point2i scrollValNew;
		// Only if we have a mouse point, and we are retrieving the same sized extent as previously
		if (mousePoint!=null && extentNew.getVolume()==canvasExtntOld.getVolume() && zoomScaleNew.equals(zoomScaleOld)) {
			scrollValNew = displayStackViewport.calcNewCrnrPosToMaintainMousePoint( mousePoint, zoomScaleOld );
		} else {
			scrollValNew = displayStackViewport.calcNewCrnrPosAfterChangeInZoom( canvasExtntOld, zoomScaleOld, extentNew, scrollVal );
			
		}
			
		setScrollBarExtnt();
		extentScrollbars.setValue( scrollValNew );

		assert( displayStackViewport.createDimensionsEntireScaled().contains( new BoundingBox(extentNew)) );
	}



	private Extent calcExtntToRetrieveScaled( ImageDim sdScaled ) {
		// We calculate the size of the region based upon the current size of the canvas (if we can)
	
		if (imageCanvas.getWidth()>0) {
			int sx = imageCanvas.getWidth();
			int sy = imageCanvas.getHeight();
			sx = Math.min(sx, sdScaled.getX() );
			sy = Math.min(sy, sdScaled.getY() );
			return new Extent(sx,sy,1);
		} else {
			ImageDim sdEntire = displayStackViewport.createDimensionsEntireScaled();
			int sx = Math.min(sdEntire.getX(), sdScaled.getX() );
			int sy = Math.min(sdEntire.getY(), sdScaled.getY() );
			return new Extent(sx,sy,1);
		}
	}


	private void updateStackViewportForImageExtnt( Extent extentImageSc ) throws OperationFailedException {
		assert( displayStackViewport.createDimensionsEntireScaled().contains( new BoundingBox(extentImageSc)) );
		
		
		Point2i scrollVal = extentScrollbars.value();
		Point3i crnrMin = new Point3i( scrollVal.getX(), scrollVal.getY(), slice );
		BoundingBox bboxView = new BoundingBox(crnrMin, extentImageSc);
		
		// We ignore nulls, as it means nothing has changed
		BufferedImage biUpdate = displayStackViewport.updateView( bboxView );
		if (biUpdate!=null) {
			imageCanvas.updated( biUpdate );
		}

		
		{
			ImageDim sd = displayStackViewport.createDimensionsEntireScaled();
			panel.setPreferredSize( new Dimension( sd.getX() + extentScrollbars.getPreferredWidth(), sd.getY() + extentScrollbars.getPreferredHeight()) );
		}
		
		
	}




	// Applies any pending changes, and then updates the image
	private void applyPendingAndUpdate( ZoomScale zoomLevel ) throws OperationFailedException {
		
		try {
			DisplayUpdate ds = imageProvider.get();
			
			// If there's no update, just update the entire image
			if (ds==null) {
				System.out.println("Null so updating everything");
				updateImage( zoomLevel );
				return;
			}
			
			// If the diplayStack has changed, then update the entire image
			if (ds.getDisplayStack()!=null) {
				
				// Then redraw parts should be null, as we have to change the displaystack anyway
				assert(ds.getRedrawParts()==null);
				
				if (slice>ds.getDisplayStack().getDimensions().getZ()) {
					slice = ds.getDisplayStack().getDimensions().getZ()-1;
				}
				
				// Let's avoid unnecessary updates
				if (displayStackViewport.getDisplayStackEntireImage()!=ds.getDisplayStack()) {
					displayStackViewport.setDisplayStackEntireImage( ds.getDisplayStack() );
				}
				updateImage( zoomLevel );
				return;
				
			}
	
			// If no redraw parts, or no existing buffer, then update everything
			if (ds.getRedrawParts()==null || !imageCanvas.hasBeenUpdated() ) {
				updateImage( zoomLevel );
				return;
			}
			
			// If we get to here then we only update the region with redraw parts
			BoundingBox bboxCurrentDisplayed = displayStackViewport.getUnzoomed().getBBox();
			
			// If we get to here, we don't change the viewport, but simply repaint some
			//   of the canvas using a section of the viewport
			for( BoundingBox bbox : ds.getRedrawParts() ) {
				
				// If it intersects with our current viewport
				if (bbox.intersection().existsWith(bboxCurrentDisplayed)) {
				
					BoundingBox bboxIntersect = bbox.intersection().withInside(
						bboxCurrentDisplayed,
						displayStackViewport.getUnzoomed().getDimensionsEntire().getExtnt()
					).orElseThrow( ()->
						new OperationFailedException("The bounding-box does not intersect with the viewport")
					);
										
					BufferedImage bi = displayStackViewport.getUnzoomed().createPartOfCurrentView( bboxIntersect );
					
					// Impose the bi on top of the existing fi
					int xCanvas = displayStackViewport.cnvrtImageXToCanvas( bboxIntersect.getCrnrMin().getX() );
					int yCanvas = displayStackViewport.cnvrtImageYToCanvas( bboxIntersect.getCrnrMin().getY() );
					
					assert( xCanvas>=0 );
					assert( yCanvas>=0 );
										
					imageCanvas.updatePart(bi,xCanvas,yCanvas);
				}
			}
		
		} catch (GetOperationFailedException | SetOperationFailedException e) {
			throw new OperationFailedException(e);
		}
	}





	private void updateViewportOnly() throws OperationFailedException {
		updateViewportOnly( displayStackViewport.getZoomScale(), null );
	}





	public void addMouseWheelListener(MouseWheelListener l) {
		imageCanvas.addMouseWheelListener(l);
	}





	public boolean isEnforceMinimumSizeAfterGuessZoom() {
		return enforceMinimumSizeAfterGuessZoom;
	}





	public void setEnforceMinimumSizeAfterGuessZoom(
			boolean enforceMinimumSizeAfterGuessZoom) {
		this.enforceMinimumSizeAfterGuessZoom = enforceMinimumSizeAfterGuessZoom;
	}





	public void addComponentListener(ComponentListener l) {
		imageCanvas.addComponentListener(l);
	}





	public void setDefaultZoomSuggestor(DefaultZoomSuggestor defaultZoomSuggestor) {
		this.defaultZoomSuggestor = defaultZoomSuggestor;
	}


	
	
	public void setScrollBarExtnt() {
		
		Extent canvasExtnt = imageCanvas.createExtnt();
		
		extentScrollbars.setVisibleAmount( canvasExtnt );
		
		// If the viewport is much smaller than our canvas size, then we allow the scrollbars to disappear
		// If we do this, when the size is similar, we can end up in endless loops of the scrollbars appearing
		//   disappearing, causing flickering, so we only do with the size is less than the approz size of the scrollbar
		
		Extent entireImageExtnt = displayStackViewport.createDimensionsEntireScaled().getExtnt();
		
		boolean alwaysAllowChangeInVisibility = minSizeDifferenceXY( canvasExtnt, entireImageExtnt ) > (extentScrollbars.maxSizeOfScrollbar() + 1);
		extentScrollbars.setMinMax( entireImageExtnt, alwaysAllowChangeInVisibility );
	}
	
	
	public int getSlice() {
		return slice;
	}

	// If the image point x,y is contained within the canvas
	public boolean canvasContainsAbs( int x, int y ) {
		return displayStackViewport.canvasContainsAbs(x,y,slice);
	}
	
	public String intensityStrAtAbs( int x, int y) {
		return displayStackViewport.intensityStrAtAbs(x, y,slice);
	}
	
	// Null means it cannot be determined
	public Optional<VoxelDataType> associatedDataType() {
		return displayStackViewport.associatedDataType();
	}

	




	public void dispose() {
		
		{
			ComponentListener[] listeners = imageCanvas.getComponentListeners();
			for( ComponentListener item : listeners ) {
				imageCanvas.removeComponentListener(item);
			}
		}
		{
			MouseWheelListener[] listeners = imageCanvas.getMouseWheelListeners();
			for( MouseWheelListener item : listeners ) {
				imageCanvas.removeMouseWheelListener(item);
			}
		}
		{
			MouseMotionListener[] listeners = imageCanvas.getMouseMotionListeners();
			for( MouseMotionListener item : listeners ) {
				imageCanvas.removeMouseMotionListener(item);
			}
		}	
		{
			MouseListener[] listeners = imageCanvas.getMouseListeners();
			for( MouseListener item : listeners ) {
				imageCanvas.removeMouseListener(item);
			}
		}
		
		displayStackViewport = null;
		imageProvider = null;
		imageCanvas = null;
		panel.removeAll();
		panel = null;
		eventList = null;
	}

	public boolean requestFocusInWindow() {
		return imageCanvas.requestFocusInWindow();
	}
	
	
	// START Getters and Setters
	
	public ImageDim getDimensions() {
		return displayStackViewport.getDimensionsEntire();
	}

	public Component getPanel() {
		return panel;
	}

	public Dimension getMinimumSize() {
		return panel.getMinimumSize();
	}

	public Dimension getPrefferedSize() {
		return panel.getPreferredSize();
	}

	public ImageRes getRes() {
		return displayStackViewport.getRes();
	}

	public ZoomScale getZoomScale() {
		return displayStackViewport.getZoomScale();
	}

	public final InputMap getInputMap() {
		return imageCanvas.getInputMap();
	}

	public final ActionMap getActionMap() {
		return imageCanvas.getActionMap();
	}
	
	
	private static int minSizeDifferenceXY( Extent first, Extent second ) {
		int diffX = first.getX() - second.getX();
		int diffY = first.getY() - second.getY();
		return Math.min(diffX, diffY);
	}
}
