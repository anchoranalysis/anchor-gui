package org.anchoranalysis.gui.frame.display.overlay;

/*-
 * #%L
 * anchor-mpp-io
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.writer.OverlayWriter;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;


/**
 * A helper for OverlayPrecalculatedCache
 * 
 * @author owen
 *
 */
class PrecalculatedOverlayList {
	
	/**
	 * A colored configuration, the main data item.  No nulls.
	 */
	private ColoredOverlayCollection overlayCollection;
	
	/**
	 * Obj-masks generated from overlayCollection.  No nulls.
	 */
	private List<PrecalcOverlay> generatedObjects;
	

	/**
	 * Bounding-boxes derived from overlayCollection.  No nulls.
	 */
	private List<BoundingBox> listBoundingBox;

	
	/**
	 * obj-masks at different zoomlevel. Can contain nulls (meaning not yet calculated).
	 * Should either be null (not-existing). Or be the same size as generatedObjects;
	 */
	private List<PrecalcOverlay> generatedObjectsZoomed;
	
	
	
	public PrecalculatedOverlayList() {
		overlayCollection = new ColoredOverlayCollection();
		generatedObjects = new ArrayList<>();
		listBoundingBox = new ArrayList<BoundingBox>();
		generatedObjectsZoomed = new ArrayList<>();
	}
	
	public PrecalculatedOverlayList(ColoredOverlayCollection overlayCollection, ImageDim dimEntireImage, OverlayWriter maskWriter) throws CreateException {
		this.overlayCollection = overlayCollection;
		rebuild(dimEntireImage, maskWriter);
	}
	
	
	public void assertListsSizeMatch() {
		assertSizesMatchSimple();
		assert( overlayCollection.size()==generatedObjects.size() );
		assert( listBoundingBox.size()==generatedObjects.size() );
		assert( generatedObjectsZoomed==null || generatedObjectsZoomed.size()==generatedObjects.size() );
	}
	
	public void assertSizesMatchSimple() {
		assert( listBoundingBox.size()==overlayCollection.size() );
	}
	
	public void assertZoomedExists() {
		assert(generatedObjectsZoomed!=null);
	}
	
	public void setOverlayCollection(ColoredOverlayCollection overlayCollection) {
		this.overlayCollection = overlayCollection;
	}
	
	public void rebuild(ImageDim dimEntireImage, OverlayWriter maskWriter) throws CreateException {
		generatedObjects = OverlayWriter.precalculate(overlayCollection, maskWriter, dimEntireImage, BinaryValues.getDefault().createByte() );
		listBoundingBox = overlayCollection.bboxList( maskWriter, dimEntireImage);
		generatedObjectsZoomed = null;
	}
	
	public void add( Overlay ol, RGBColor color, PrecalcOverlay precalc, BoundingBox bbox, PrecalcOverlay precalcZoomed ) {
		overlayCollection.add( ol, color );
		generatedObjects.add(precalc);
		listBoundingBox.add( bbox );
		if (generatedObjectsZoomed!=null) {
			generatedObjectsZoomed.add(precalcZoomed);
		}
	}
	
	public void remove( int index ) {
		overlayCollection.remove(index);
		generatedObjects.remove(index);
		listBoundingBox.remove(index);
		
		if (generatedObjectsZoomed!=null) {
			generatedObjectsZoomed.remove(index);
		}
	}
	
	public void setZoomedToNull() {
		generatedObjectsZoomed = createCollectionWithNulls( size() );
	}

	public Overlay getOverlay(int index) {
		return overlayCollection.get(index);
	}

	public RGBColor getColor(int index) {
		return overlayCollection.getColor(index);
	}

	public PrecalcOverlay getPrecalcOverlay(int index) {
		return generatedObjects.get(index);
	}
	
	public PrecalcOverlay getPrecalc(int index) {
		return generatedObjects.get(index);
	}
	
	public PrecalcOverlay getPrecalcZoomed(int index) {
		return generatedObjectsZoomed.get(index);
	}
	
	public List<PrecalcOverlay> getListGeneratedObjects() {
		return generatedObjects;
	}

	public List<PrecalcOverlay> getListGeneratedObjectsZoomed() {
		return generatedObjectsZoomed;
	}
	
	public List<BoundingBox> getListBoundingBox() {
		return listBoundingBox;
	}
	
	public boolean hasGeneratedObjectsZoomed() {
		return generatedObjectsZoomed!=null;
	}

	public ColoredOverlayCollection getOverlayCollection() {
		return overlayCollection;
	}

	public BoundingBox getBBox(int index) {
		return listBoundingBox.get(index);
	}

	public int size() {
		return listBoundingBox.size();
	}

	public PrecalcOverlay setPrecalcZoomed(int index, PrecalcOverlay element) {
		return generatedObjectsZoomed.set(index, element);
	}
	
	private static List<PrecalcOverlay> createCollectionWithNulls( int size ) {
		List<PrecalcOverlay> out = new ArrayList<>();
		for( int i=0; i<size; i++ ) {
			out.add(null);
		}
		return out;
	}
}