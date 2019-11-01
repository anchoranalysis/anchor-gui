package org.anchoranalysis.gui.retrieveelements;

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


import java.util.Collection;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjMaskFactory;
import org.anchoranalysis.image.io.objs.ObjMaskCollectionWriter;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.collection.SubfolderGenerator;
import org.anchoranalysis.io.generator.sequence.CollectionGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;

import ch.ethz.biol.cell.mpp.cfg.Cfg;

public class RetrieveElementsOverlayCollection extends RetrieveElements {

	private OverlayCollection currentSelectedObjects;
	private OverlayCollection currentObjects;

	@Override
	public void addToPopUp(IAddToExportSubMenu popUp) {

		if (currentObjects!=null) {
			Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( currentObjects );
			addAllMarksAsConfiguration( popUp, cfg );
    	}
		
		if (currentSelectedObjects!=null) {
			
			// Selected Marks as Configuration
			Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( currentSelectedObjects );
			ObjMaskCollection objs = OverlayCollectionObjMaskFactory.objsFromOverlays( currentSelectedObjects );
			
			// Selected Marks as Objects
			addSelectedObjects( popUp, objs );
			addSelectedMarksSerialized( popUp, cfg );
			addSelectedMarksAsConfiguration( popUp, cfg );
		}
	}
	
	
	private void addSelectedObjects( IAddToExportSubMenu popUp, ObjMaskCollection objs ) {
		IterableGenerator<ObjMaskCollection> generator = ObjMaskCollectionWriter.generator();
		popUp.addExportItem(
			generator,
			objs,
			"selectedObjects",
			"Selected Objects",
			SubfolderGenerator.createManifestDescription("objMaskCollection"),
			1
		);
	}
	
	private void addSelectedMarksSerialized( IAddToExportSubMenu popUp, Cfg cfg ) {
		Collection<Mark> marks = cfg.createSet();
		ObjectOutputStreamGenerator<Mark> generatorMark = new ObjectOutputStreamGenerator<>("mark");
		CollectionGenerator<Mark> generatorCollection = new CollectionGenerator<>("selectedMarksObjects", generatorMark, popUp.getOutputManager().getDelegate(), 3, true);
		popUp.addExportItem( generatorCollection, marks, "selectedMarksObjects", "Selected Marks [Serialized]", generatorMark.createManifestDescription(), marks.size() );
	}
	
	private void addSelectedMarksAsConfiguration( IAddToExportSubMenu popUp, Cfg cfg ) {
		ObjectOutputStreamGenerator<Cfg> generatorCfg = new ObjectOutputStreamGenerator<>("cfg");
		popUp.addExportItem( generatorCfg, cfg, "selectedMarksCfg", "Selected Marks as Configuration", generatorCfg.createManifestDescription(), 1 );
	}
	
	private void addAllMarksAsConfiguration( IAddToExportSubMenu popUp, Cfg cfg ) {
		ObjectOutputStreamGenerator<Cfg> generator = new ObjectOutputStreamGenerator<>("cfg");
		popUp.addExportItem( generator, cfg, "allMarksCfg", "All Marks as Configuration", generator.createManifestDescription(), 1 );
	}
	
	public void setCurrentSelectedObjects(OverlayCollection currentSelectedObjects) {
		this.currentSelectedObjects = currentSelectedObjects;
	}

	public void setCurrentObjects(OverlayCollection currentObjects) {
		this.currentObjects = currentObjects;
	}

}
