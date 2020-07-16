/* (C)2020 */
package org.anchoranalysis.gui.annotation.mark;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.videostats.internalframe.annotator.InternalFrameAnnotator;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkProposerEvaluatorDimensions;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkSphereOnPointProposerEvaluator;
import org.anchoranalysis.image.extent.ImageDimensions;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class EvaluatorFactory {

    public static Optional<EvaluatorWithContext> createGuessEvaluator(
            MarkProposer markProposerGuess,
            MarkEvaluatorResolved markEvaluatorResolved,
            RegionMap regionMap,
            ToolErrorReporter errorReporter) {

        // If we have no guess proposer, then we return null
        if (markProposerGuess == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    new EvaluatorWithContext(
                            new MarkProposerEvaluatorDimensions(markProposerGuess, false),
                            markEvaluatorResolved.getNRGStack(),
                            markEvaluatorResolved.getCfgGen(),
                            regionMap));
        } catch (GetOperationFailedException e) {
            errorReporter.showError(
                    InternalFrameAnnotator.class, "Cannot create guess evaluator", e.toString());
            return Optional.empty();
        }
    }

    public static Optional<EvaluatorWithContext> createSelectPointsEvaluator(
            ImageDimensions dimViewer,
            MarkEvaluatorResolved markEvaluatorResolved,
            RegionMap regionMap,
            ToolErrorReporter errorReporter) {
        try {
            return Optional.of(
                    new EvaluatorWithContext(
                            new MarkSphereOnPointProposerEvaluator(dimViewer),
                            markEvaluatorResolved.getNRGStack(),
                            markEvaluatorResolved.getCfgGen(),
                            regionMap));
        } catch (GetOperationFailedException e) {
            errorReporter.showError(
                    InternalFrameAnnotator.class,
                    "Cannot create select-points evaluator",
                    e.toString());
            return Optional.empty();
        }
    }
}
