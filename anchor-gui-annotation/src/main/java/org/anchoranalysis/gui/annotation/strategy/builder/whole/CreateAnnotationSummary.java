/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.wholeimage.WholeImageLabelAnnotation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.plugin.annotation.bean.strategy.ReadAnnotationFromFile;

class CreateAnnotationSummary {

    private LabelColorMap colors;

    public CreateAnnotationSummary(LabelColorMap colors) {
        super();
        this.colors = colors;
    }

    public AnnotationSummary apply(Path path) throws CreateException {
        if (Files.exists(path)) {
            return createSummaryForExistingAnnotation(path);
        } else {
            return createSummaryForMissingAnnotation();
        }
    }

    private AnnotationSummary createSummaryForExistingAnnotation(Path path) throws CreateException {

        Optional<WholeImageLabelAnnotation> ann = ReadAnnotationFromFile.readAssumeExists(path);

        if (!ann.isPresent()) {
            throw new CreateException("Failed to read a label for the annotation");
        }

        AnnotationSummary as = new AnnotationSummary();
        as.setShortDescription(ann.get().getLabel());
        as.setColor(colors.get(ann.get().getLabel()));
        as.setExistsFinished(true);
        return as;
    }

    private AnnotationSummary createSummaryForMissingAnnotation() {
        AnnotationSummary as = new AnnotationSummary();
        as.setColor(Color.WHITE);
        as.setExistsFinished(false);
        return as;
    }
}
