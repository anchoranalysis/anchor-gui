/* (C)2020 */
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
