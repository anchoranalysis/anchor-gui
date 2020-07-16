/* (C)2020 */
package org.anchoranalysis.gui.annotation.export;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.JFrame;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationWriter;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationWriterGUI;
import org.anchoranalysis.io.error.AnchorIOException;
import org.apache.commons.io.FilenameUtils;

public class ExportScaledMarks extends ExportAnnotation {

    private MarkAnnotationReader reader = new MarkAnnotationReader(true);
    private Path annotationPath;

    public ExportScaledMarks(Path annotationPath) {
        super();
        assert (annotationPath != null);
        // Read annotation
        this.annotationPath = annotationPath;
    }

    @Override
    public boolean isPromptForScalingNeeded() {
        return true;
    }

    @Override
    public Path proposedExportPath() {
        // Appends _export to a path
        return suffixBasename(annotationPath, "_export");
    }

    @Override
    public void exportToPath(
            Path path,
            double scaleFactor,
            JFrame parentFrame,
            AnnotationRefresher annotationRefresher)
            throws OperationFailedException {

        try {
            // Read file
            Optional<MarkAnnotation> ann = reader.read(path);

            if (!ann.isPresent()) {
                throw new OperationFailedException("There is no annotation to export");
            }

            // Scale annotation
            if (scaleFactor != 1.0) {
                ann.get().scaleXY(scaleFactor);
            }

            MarkAnnotationWriter writer = new MarkAnnotationWriter();
            writer.setDisablePathModification(true);
            AnnotationWriterGUI<MarkAnnotation> writerGUI =
                    new AnnotationWriterGUI<>(writer, annotationRefresher, Optional.empty());
            writerGUI.saveAnnotation(ann.get(), path, parentFrame);
        } catch (AnchorIOException | OptionalOperationUnsupportedException e) {
            throw new OperationFailedException(e);
        }
    }

    private static Path suffixBasename(Path path, String suffix) {

        String filePath = path.toString();

        String basename = FilenameUtils.getBaseName(filePath);
        String extension = FilenameUtils.getExtension(filePath);
        File dir = path.toFile().getParentFile();

        return dir.toPath().resolve(basename + suffix + "." + extension);
    }
}
