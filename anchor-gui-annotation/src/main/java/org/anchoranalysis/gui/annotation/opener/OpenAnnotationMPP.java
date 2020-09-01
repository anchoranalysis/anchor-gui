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

package org.anchoranalysis.gui.annotation.opener;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.annotation.mark.RejectionReason;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.PartitionedMarks;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.mark.MarkCollection;

public class OpenAnnotationMPP implements OpenAnnotation {

    private MarkAnnotationReader annotationReader;
    private Path annotationPath;
    private Optional<Path> defaultMarksPath;

    public OpenAnnotationMPP(
            Path annotationPath,
            Optional<Path> defaultMarksPath,
            MarkAnnotationReader annotationReader) {
        super();
        this.annotationPath = annotationPath;
        this.defaultMarksPath = defaultMarksPath;
        this.annotationReader = annotationReader;
    }

    @Override
    public InitAnnotation open(boolean useDefaultMarks, Logger logger)
            throws VideoStatsModuleCreateException {

        // We try to read an existing annotation
        Optional<MarkAnnotation> annotationExst = readAnnotation(annotationPath);

        if (annotationExst.isPresent()) {
            return readMarksFromAnnotation(annotationExst.get());

        } else if (defaultMarksPath.isPresent() && useDefaultMarks) {
            return readDefaultMarks(defaultMarksPath.get(), logger);

        } else {
            logger.messageLogger().logFormatted("No marks to open for annotation");
            return new InitAnnotation(Optional.empty());
        }
    }

    public boolean isUseDefaultPromptNeeded() {
        return !annotationReader.annotationExistsCorrespondTo(annotationPath)
                && defaultMarksPath.isPresent();
    }

    private static InitAnnotation readMarksFromAnnotation(MarkAnnotation annotationExst) {

        PartitionedMarks initMarks =
                new PartitionedMarks(annotationExst.getMarks(), annotationExst.getMarksReject());

        if (annotationExst.isAccepted()) {
            return new InitAnnotation(Optional.of(annotationExst), initMarks);
        } else {
            String initMsg = errorMessage(annotationExst.getRejectionReason());
            return new InitAnnotation(Optional.of(annotationExst), initMarks, initMsg);
        }
    }

    private InitAnnotation readDefaultMarks(Path defaultMarksPath, Logger logger) {
        try {
            MarkCollection defaultMarks = annotationReader.readDefaultMarks(defaultMarksPath);
            return new InitAnnotation(
                    Optional.empty(), new PartitionedMarks(defaultMarks, new MarkCollection()));
        } catch (DeserializationFailedException e) {
            logger.messageLogger().logFormatted("Cannot open defaultMarks at %s", defaultMarksPath);
            logger.errorReporter().recordError(AnnotatorModuleCreator.class, e);
            return new InitAnnotation(Optional.empty());
        }
    }

    private static String errorMessage(RejectionReason reason) {
        StringBuilder sb = new StringBuilder("Annotation was SKIPPED due to ");
        switch (reason) {
            case INCORRECT_BOUNDARY:
                sb.append("an incorrect boundary");
                break;
            case POOR_IMAGE_QUALITY:
                sb.append("poor image quality");
                break;
            case INCORRECT_IMAGE_CONTENT:
                sb.append("incorrect image content");
                break;
            case OTHER:
                sb.append("undefined reasons");
                break;
            default:
                assert false;
        }
        return sb.toString();
    }

    private Optional<MarkAnnotation> readAnnotation(Path annotationPath)
            throws VideoStatsModuleCreateException {
        try {
            // We try to read an existing annotation
            // If we can read an annotation let's do it
            return annotationReader.read(annotationPath);
        } catch (AnchorIOException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }
}
