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

package org.anchoranalysis.gui.annotation.strategy.builder.whole;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.image.ImageLabelAnnotation;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.io.input.InputReadFailedException;
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

        try {
            Optional<ImageLabelAnnotation> ann = ReadAnnotationFromFile.readAssumeExists(path);
    
            if (!ann.isPresent()) {
                throw new CreateException("Failed to read a label for the annotation");
            }
    
            AnnotationSummary as = new AnnotationSummary();
            as.setShortDescription(ann.get().getLabel());
            as.setColor(colors.get(ann.get().getLabel()));
            as.setExistsFinished(true);
            return as;
        } catch (InputReadFailedException e) {
            throw new CreateException(e);
        }
    }

    private AnnotationSummary createSummaryForMissingAnnotation() {
        AnnotationSummary as = new AnnotationSummary();
        as.setColor(Color.WHITE);
        as.setExistsFinished(false);
        return as;
    }
}
