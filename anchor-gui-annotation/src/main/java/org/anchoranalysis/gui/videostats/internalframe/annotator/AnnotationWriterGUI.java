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

package org.anchoranalysis.gui.videostats.internalframe.annotator;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.JOptionPane;
import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.annotation.io.AnnotationWriter;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;

public class AnnotationWriterGUI<T extends Annotation> {

    private AnnotationRefresher annotationRefresher;
    private Optional<SaveMonitor> saveMonitor;
    private AnnotationWriter<T> writer;

    // saveMonitor is optional
    public AnnotationWriterGUI(
            AnnotationWriter<T> writer,
            AnnotationRefresher annotationRefresher,
            Optional<SaveMonitor> saveMonitor) {
        super();
        this.annotationRefresher = annotationRefresher;
        this.saveMonitor = saveMonitor;
        this.writer = writer;
    }

    /**
     * Saves an annotation to the filesystem to annotationPath (or a slightly modified path if the
     * annotation is unfinished)
     *
     * @param annotation annotation to be saved
     * @param annotationPath our target path (altered if the annotation is unfinished)
     */
    public boolean saveAnnotation(T annotation, Path annotationPath, Component dialogParent) {

        try {
            writer.write(annotation, annotationPath);
        } catch (IOException e) {

            // custom title, error icon
            JOptionPane.showMessageDialog(
                    dialogParent,
                    String.format(
                            "Writing annotation to file '%s' failed%n%n%s",
                            annotationPath, e.toString()),
                    "Error saving annotation",
                    JOptionPane.ERROR_MESSAGE);

            return false;
        }

        annotationRefresher.refreshAnnotation();

        if (saveMonitor.isPresent()) {
            saveMonitor.get().markAsSaved();
        }

        return true;
    }

    public void dispose() {
        annotationRefresher = null;
        saveMonitor = Optional.empty();
    }
}
