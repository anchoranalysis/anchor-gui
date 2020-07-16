/* (C)2020 */
package org.anchoranalysis.gui.annotation.opener;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.annotation.mark.MarkAnnotation;
import org.anchoranalysis.annotation.mark.RejectionReason;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.DualCfg;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;

public class OpenAnnotationMPP implements IOpenAnnotation {

    private MarkAnnotationReader annotationReader;
    private Path annotationPath;
    private Optional<Path> defaultCfgPath;

    public OpenAnnotationMPP(
            Path annotationPath,
            Optional<Path> defaultCfgPath,
            MarkAnnotationReader annotationReader) {
        super();
        this.annotationPath = annotationPath;
        this.defaultCfgPath = defaultCfgPath;
        this.annotationReader = annotationReader;
    }

    @Override
    public InitAnnotation open(boolean useDefaultCfg, Logger logger)
            throws VideoStatsModuleCreateException {

        // We try to read an existing annotation
        Optional<MarkAnnotation> annotationExst = readAnnotation(annotationPath);

        if (annotationExst.isPresent()) {
            return readCfgFromAnnotation(annotationExst.get());

        } else if (defaultCfgPath.isPresent() && useDefaultCfg) {
            return readDefaultCfg(defaultCfgPath.get(), logger);

        } else {
            logger.messageLogger().logFormatted("No cfg to open for annotation");
            return new InitAnnotation(Optional.empty());
        }
    }

    public boolean isUseDefaultPromptNeeded() {
        return !annotationReader.annotationExistsCorrespondTo(annotationPath)
                && defaultCfgPath.isPresent();
    }

    private static InitAnnotation readCfgFromAnnotation(MarkAnnotation annotationExst) {

        DualCfg initCfg = new DualCfg(annotationExst.getCfg(), annotationExst.getCfgReject());

        if (annotationExst.isAccepted()) {
            return new InitAnnotation(Optional.of(annotationExst), initCfg);
        } else {
            String initMsg = genErrorMsg(annotationExst.getRejectionReason());
            return new InitAnnotation(Optional.of(annotationExst), initCfg, initMsg);
        }
    }

    private InitAnnotation readDefaultCfg(Path defaultCfgPath, Logger logger) {
        try {
            Cfg defaultCfg = annotationReader.readDefaultCfg(defaultCfgPath);
            return new InitAnnotation(Optional.empty(), new DualCfg(defaultCfg, new Cfg()));
        } catch (DeserializationFailedException e) {
            logger.messageLogger().logFormatted("Cannot open defaultCfg at %s", defaultCfgPath);
            logger.errorReporter().recordError(AnnotatorModuleCreator.class, e);
            return new InitAnnotation(Optional.empty());
        }
    }

    private static String genErrorMsg(RejectionReason reason) {
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
