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

package org.anchoranalysis.gui.retrieveelements;

import java.util.Collection;
import java.util.Optional;
import org.anchoranalysis.image.io.objects.ObjectCollectionWriter;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.generator.collection.CollectionGenerator;
import org.anchoranalysis.io.generator.serialized.ObjectOutputStreamGenerator;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.collection.OverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollectionObjectFactory;

public class RetrieveElementsOverlayCollection extends RetrieveElements {

    private OverlayCollection currentSelectedObjects;
    private OverlayCollection currentObjects;

    @Override
    public void addToPopUp(AddToExportSubMenu popUp) {

        if (currentObjects != null) {
            MarkCollection marks = OverlayCollectionMarkFactory.marksFromOverlays(currentObjects);
            addAllMarksAsConfiguration(popUp, marks);
        }

        if (currentSelectedObjects != null) {

            // Selected Marks as Configuration
            MarkCollection marks =
                    OverlayCollectionMarkFactory.marksFromOverlays(currentSelectedObjects);
            ObjectCollection objects =
                    OverlayCollectionObjectFactory.objectsFromOverlays(currentSelectedObjects);

            // Selected Marks as Objects
            addSelectedObjects(popUp, objects);
            addSelectedMarksSerialized(popUp, marks);
            addSelectedMarksAsConfiguration(popUp, marks);
        }
    }

    private void addSelectedObjects(AddToExportSubMenu popUp, ObjectCollection objects) {
        popUp.addExportItem(
                ObjectCollectionWriter.generator(),
                objects,
                "selectedObjects",
                "Selected Objects",
                Optional.of(
                        CollectionGenerator.createManifestDescription(
                                ObjectCollectionWriter.MANIFEST_DESCRIPTION)),
                1);
    }

    private void addSelectedMarksSerialized(AddToExportSubMenu popUp, MarkCollection marks) {
        Collection<Mark> marksCreated = marks.createSet();
        ObjectOutputStreamGenerator<Mark> generatorMark =
                new ObjectOutputStreamGenerator<>(Optional.of("mark"));
        CollectionGenerator<Mark> generatorCollection =
                new CollectionGenerator<>(
                        generatorMark,
                        "selectedMarksObjects"
                );
        popUp.addExportItem(
                generatorCollection,
                marksCreated,
                "selectedMarksObjects",
                "Selected Marks [Serialized]",
                generatorMark.createManifestDescription(),
                marksCreated.size());
    }

    private void addSelectedMarksAsConfiguration(AddToExportSubMenu popUp, MarkCollection marks) {
        ObjectOutputStreamGenerator<MarkCollection> generatorMarks =
                new ObjectOutputStreamGenerator<>(Optional.of("marks"));
        popUp.addExportItem(
                generatorMarks,
                marks,
                "selectedMarksMarks",
                "Selected Marks as Configuration",
                generatorMarks.createManifestDescription(),
                1);
    }

    private void addAllMarksAsConfiguration(AddToExportSubMenu popUp, MarkCollection marks) {
        ObjectOutputStreamGenerator<MarkCollection> generator =
                new ObjectOutputStreamGenerator<>(Optional.of("marks"));
        popUp.addExportItem(
                generator,
                marks,
                "allMarksMarks",
                "All Marks as Configuration",
                generator.createManifestDescription(),
                1);
    }

    public void setCurrentSelectedObjects(OverlayCollection currentSelectedObjects) {
        this.currentSelectedObjects = currentSelectedObjects;
    }

    public void setCurrentObjects(OverlayCollection currentObjects) {
        this.currentObjects = currentObjects;
    }
}
