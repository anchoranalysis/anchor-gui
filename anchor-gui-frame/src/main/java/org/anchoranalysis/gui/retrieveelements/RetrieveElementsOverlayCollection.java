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
import java.util.Optional;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjectFactory;
import org.anchoranalysis.image.io.objects.ObjectCollectionWriter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.collection.SubfolderGenerator;
import org.anchoranalysis.io.generator.sequence.CollectionGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;

public class RetrieveElementsOverlayCollection extends RetrieveElements {

	private OverlayCollection currentSelectedObjects;
	private OverlayCollection currentObjects;

	@Override
	public void addToPopUp(AddToExportSubMenu popUp) {

		if (currentObjects!=null) {
			Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( currentObjects );
			addAllMarksAsConfiguration( popUp, cfg );
    	}
		
		if (currentSelectedObjects!=null) {
			
			// Selected Marks as Configuration
			Cfg cfg = OverlayCollectionMarkFactory.cfgFromOverlays( currentSelectedObjects );
			ObjectCollection objects = OverlayCollectionObjectFactory.objectsFromOverlays( currentSelectedObjects );
			
			// Selected Marks as Objects
			addSelectedObjects( popUp, objects );
			addSelectedMarksSerialized( popUp, cfg );
			addSelectedMarksAsConfiguration( popUp, cfg );
		}
	}
	
	
	private void addSelectedObjects( AddToExportSubMenu popUp, ObjectCollection objects ) {
		popUp.addExportItem(
			ObjectCollectionWriter.generator(),
			objects,
			"selectedObjects",
			"Selected Objects",
			Optional.of(
				SubfolderGenerator.createManifestDescription(ObjectCollectionWriter.MANIFEST_DESCRIPTION)
			),
			1
		);
	}
	
	private void addSelectedMarksSerialized( AddToExportSubMenu popUp, Cfg cfg ) {
		Collection<Mark> marks = cfg.createSet();
		ObjectOutputStreamGenerator<Mark> generatorMark = new ObjectOutputStreamGenerator<>(
			Optional.of("mark")
		);
		CollectionGenerator<Mark> generatorCollection = new CollectionGenerator<>("selectedMarksObjects", generatorMark, popUp.getOutputManager().getDelegate(), 3, true);
		popUp.addExportItem( generatorCollection, marks, "selectedMarksObjects", "Selected Marks [Serialized]", generatorMark.createManifestDescription(), marks.size() );
	}
	
	private void addSelectedMarksAsConfiguration( AddToExportSubMenu popUp, Cfg cfg ) {
		ObjectOutputStreamGenerator<Cfg> generatorCfg = new ObjectOutputStreamGenerator<>(
			Optional.of("cfg")
		);
		popUp.addExportItem( generatorCfg, cfg, "selectedMarksCfg", "Selected Marks as Configuration", generatorCfg.createManifestDescription(), 1 );
	}
	
	private void addAllMarksAsConfiguration( AddToExportSubMenu popUp, Cfg cfg ) {
		ObjectOutputStreamGenerator<Cfg> generator = new ObjectOutputStreamGenerator<>(
			Optional.of("cfg")
		);
		popUp.addExportItem( generator, cfg, "allMarksCfg", "All Marks as Configuration", generator.createManifestDescription(), 1 );
	}
	
	public void setCurrentSelectedObjects(OverlayCollection currentSelectedObjects) {
		this.currentSelectedObjects = currentSelectedObjects;
	}

	public void setCurrentObjects(OverlayCollection currentObjects) {
		this.currentObjects = currentObjects;
	}

}
