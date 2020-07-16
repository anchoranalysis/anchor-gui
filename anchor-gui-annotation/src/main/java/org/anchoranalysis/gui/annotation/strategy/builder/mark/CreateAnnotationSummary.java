/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.annotation.state.AnnotationProgressState;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.io.error.AnchorIOException;

class CreateAnnotationSummary {

    private static Color colorRed = new Color(99, 00, 00);
    private static Color colorGreen = new Color(00, 99, 00);
    private static Color colorGreenSkipped = new Color(41, 171, 135);
    private static Color colorOrange = new Color(207, 83, 00);
    // private static Color colorOrangeDark = new Color(255, 153, 00);

    public static AnnotationSummary apply(
            Path annotationPath, MarkAnnotationReader annotationReader) throws CreateException {
        AnnotationSummary as = new AnnotationSummary();

        AnnotationProgressState aps = annotationProgressState(annotationPath);

        if (aps == AnnotationProgressState.ANNOTATION_FINISHED) {

            try {
                MarkAnnotation a =
                        annotationReader
                                .read(annotationPath)
                                .orElseThrow(
                                        () ->
                                                new CreateException(
                                                        String.format(
                                                                "No annotation exists at the specified path: %s",
                                                                annotationPath)));
                as.setShortDescription(shortDescription(a));
                as.setColor(color(aps, a.isAccepted()));
                as.setExistsFinished(true);

            } catch (AnchorIOException e) {
                throw new CreateException(e);
            }
        } else {
            as.setColor(color(aps, false));
            as.setShortDescription("");
            as.setExistsFinished(false);
        }

        return as;
    }

    private static String shortDescription(MarkAnnotation annotation) {
        if (annotation.isAccepted()) {
            return Integer.toString(annotation.getCfg().size());
        } else {
            return ""; // Empty-string if not-accepted
        }
    }

    private static AnnotationProgressState annotationProgressState(Path annotationPath) {

        if (Files.exists(annotationPath)) {
            return AnnotationProgressState.ANNOTATION_FINISHED;
        } else if (Files.exists(Paths.get(annotationPath + ".temp"))) {
            return AnnotationProgressState.ANNOTATION_UNFINISHED;
        } else {
            return AnnotationProgressState.NO_ANNOTATION;
        }
    }

    private static Color color(AnnotationProgressState state, boolean accepted) {

        if (state == AnnotationProgressState.ANNOTATION_FINISHED) {

            if (accepted) {
                return colorGreen;
            } else {
                return colorGreenSkipped;
            }

        } else if (state == AnnotationProgressState.ANNOTATION_UNFINISHED) {
            return colorOrange;
        } else {
            return colorRed;
        }
    }
}
