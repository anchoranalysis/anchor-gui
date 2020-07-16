/* (C)2020 */
package org.anchoranalysis.gui.annotation.dropdown;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationDeleter;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.videostats.operation.VideoStatsOperation;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;

class DeleteAnnotationOperation implements VideoStatsOperation {

    private JFrame parentFrame;
    private Path pathToDelete;
    private AnnotationRefresher annotationRefresher;

    public DeleteAnnotationOperation(
            JFrame parentFrame, Path pathToDelete, AnnotationRefresher annotationRefresher) {
        super();
        this.parentFrame = parentFrame;
        this.pathToDelete = pathToDelete;
        this.annotationRefresher = annotationRefresher;
    }

    @Override
    public String getName() {
        return "Delete annotation";
    }

    @Override
    public void execute(boolean withMessages) {
        try {
            new MarkAnnotationDeleter().delete(pathToDelete);
            annotationRefresher.refreshAnnotation();
        } catch (IOException e) {
            // custom title, error icon
            JOptionPane.showMessageDialog(
                    parentFrame,
                    String.format(
                            "Deleting annotation at file '%s' failed%n%n%s",
                            pathToDelete, e.toString()),
                    "Error deleting annotation",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return Optional.empty();
    }
}
