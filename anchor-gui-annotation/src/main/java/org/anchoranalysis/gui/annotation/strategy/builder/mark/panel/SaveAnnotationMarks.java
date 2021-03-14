/*-
 * #%L
 * anchor-gui-annotation
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

package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import java.awt.Component;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import lombok.AllArgsConstructor;
import org.anchoranalysis.annotation.mark.DualMarks;
import org.anchoranalysis.annotation.mark.DualMarksAnnotation;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.gui.annotation.mark.RejectionReason;
import org.anchoranalysis.gui.annotation.save.ISaveAnnotation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationWriterGUI;

@AllArgsConstructor
class SaveAnnotationMarks implements ISaveAnnotation<DualMarksAnnotation<RejectionReason>> {

    private Path annotationPath;

    @Override
    public void saveFinished(
            DualMarks query,
            AnnotationWriterGUI<DualMarksAnnotation<RejectionReason>> annotationWriter,
            JComponent dialogParent) {

        saveAnnotation(
                annotationWriter, annotation -> annotation.assignAccepted(query), dialogParent);
    }

    @Override
    public void savePaused(
            DualMarks query,
            AnnotationWriterGUI<DualMarksAnnotation<RejectionReason>> annotationWriter,
            JComponent dialogParent) {

        saveAnnotation(
                annotationWriter, annotation -> annotation.assignPaused(query), dialogParent);
    }

    @Override
    public void skipAnnotation(
            DualMarks query,
            AnnotationWriterGUI<DualMarksAnnotation<RejectionReason>> annotationWriter,
            JComponent dialogParent) {

        promptForRejectionReason()
                .ifPresent(
                        rejectionReason ->
                                saveAnnotation(
                                        annotationWriter,
                                        annotation ->
                                                annotation.assignRejected(query, rejectionReason),
                                        dialogParent));
    }

    private void saveAnnotation(
            AnnotationWriterGUI<DualMarksAnnotation<RejectionReason>> annotationWriter,
            Consumer<DualMarksAnnotation<RejectionReason>> opAnnotation,
            Component dialogParent) {

        DualMarksAnnotation<RejectionReason> annotation = new DualMarksAnnotation<>();
        opAnnotation.accept(annotation);
        annotationWriter.saveAnnotation(annotation, annotationPath, dialogParent);
    }

    // Returns null if cancelled
    private static Optional<RejectionReason> promptForRejectionReason() {

        String[] choices = {
            "Boundary is incorrect",
            "Image quality is too poor",
            "Incorrect image content",
            "Other",
            "Cancel"
        };

        int response =
                JOptionPane.showOptionDialog(
                        null // Center in window.
                        ,
                        "Why do you skip annotating?" // Message
                        ,
                        "Skip annotation" // Title in titlebar
                        ,
                        JOptionPane.DEFAULT_OPTION // Option type
                        ,
                        JOptionPane.PLAIN_MESSAGE // messageType
                        ,
                        null // Icon (none)
                        ,
                        choices // Button text as above.
                        ,
                        "Boundary is incorrect" // Default button's label
                        );

        switch (response) {
            case 0:
                return Optional.of(RejectionReason.INCORRECT_BOUNDARY);
            case 1:
                return Optional.of(RejectionReason.POOR_IMAGE_QUALITY);
            case 2:
                return Optional.of(RejectionReason.INCORRECT_IMAGE_CONTENT);
            case 3:
                return Optional.of(RejectionReason.OTHER);
            case 4:
            case -1:
                return Optional.empty();
            default:
                throw new AnchorImpossibleSituationException();
        }
    }
}
