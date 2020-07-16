/* (C)2020 */
package org.anchoranalysis.gui.annotation.mark;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;

public class MarkAnnotator {

    private MarkEvaluatorResolved markEvaluatorResolved;
    private MarkProposer markProposerGuess;
    private PointsFitter pointsFitterSelectPoints;

    private NamedProviderStore<Stack> backgroundStacks;

    public MarkAnnotator(
            MarkProposerStrategy annotationStrategy,
            MarkEvaluatorSetForImage markEvaluatorSet,
            Logger logger)
            throws CreateException {

        markEvaluatorResolved = setupMarkEvaluator(annotationStrategy, markEvaluatorSet);

        MPPInitParams soMPP =
                setupEvaluatorAndPointsFitter(markEvaluatorResolved, annotationStrategy);

        pointsFitterSelectPoints = extractPointsFitter(annotationStrategy, soMPP);

        // Nullable
        markProposerGuess = setupGuess(soMPP, annotationStrategy, logger);

        this.backgroundStacks = soMPP.getImage().getStackCollection();
    }

    public RegionMap getRegionMap() {
        return markEvaluatorResolved.getNrgScheme().getRegionMap();
    }

    public NamedProviderStore<Stack> getBackgroundStacks() {
        return backgroundStacks;
    }

    public Optional<EvaluatorWithContext> createGuessEvaluator(ToolErrorReporter errorReporter) {
        return EvaluatorFactory.createGuessEvaluator(
                markProposerGuess, markEvaluatorResolved, getRegionMap(), errorReporter);
    }

    public Optional<EvaluatorWithContext> createSelectPointsEvaluator(
            ImageDimensions dimViewer, ToolErrorReporter errorReporter) {
        return EvaluatorFactory.createSelectPointsEvaluator(
                dimViewer, markEvaluatorResolved, getRegionMap(), errorReporter);
    }

    public PointsFitter getPointsFitterSelectPoints() {
        return pointsFitterSelectPoints;
    }

    private static MPPInitParams setupEvaluatorAndPointsFitter(
            MarkEvaluatorResolved markEvaluator, MarkProposerStrategy annotationStrategy)
            throws CreateException {
        return markEvaluator.getProposerSharedObjectsOperation().doOperation();
    }

    private static PointsFitter extractPointsFitter(
            MarkProposerStrategy annotationStrategy, MPPInitParams soMPP) throws CreateException {
        try {
            return soMPP.getPoints()
                    .getPointsFitterSet()
                    .getException(annotationStrategy.getPointsFitterName());
        } catch (NamedProviderGetException e) {
            throw new CreateException(e);
        }
    }

    private static MarkProposer setupGuess(
            MPPInitParams soMPP, MarkProposerStrategy annotationStrategy, Logger logger) {
        try {
            return soMPP.getMarkProposerSet()
                    .getException(annotationStrategy.getMarkProposerName());
        } catch (NamedProviderGetException e) {
            logger.messageLogger().log("Proceeding without 'Guess Tool' as an error occured");
            logger.errorReporter()
                    .recordError(AnnotatorModuleCreator.class, e.summarize().toString());
            return null;
        }
    }

    private static MarkEvaluatorResolved setupMarkEvaluator(
            MarkProposerStrategy annotationStrategy, MarkEvaluatorSetForImage markEvaluatorSet)
            throws CreateException {
        try {
            if (annotationStrategy.getMarkEvaluator() != null) {
                markEvaluatorSet.add(
                        annotationStrategy.getMarkEvaluatorName(),
                        annotationStrategy.getMarkEvaluator());
            }

            return markEvaluatorSet.get(annotationStrategy.getMarkEvaluatorName());
        } catch (GetOperationFailedException | OperationFailedException e1) {
            throw new CreateException(e1);
        }
    }
}
