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

package org.anchoranalysis.gui.annotation.mark;

import java.util.Optional;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.identifier.provider.NamedProviderGetException;
import org.anchoranalysis.core.identifier.provider.store.NamedProviderStore;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
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

        MarksInitialization initialization = setupEvaluatorAndPointsFitter(markEvaluatorResolved);

        pointsFitterSelectPoints = extractPointsFitter(annotationStrategy, initialization);

        // Nullable
        markProposerGuess = setupGuess(initialization, annotationStrategy, logger);

        this.backgroundStacks = initialization.getImage().stacks();
    }

    public RegionMap getRegionMap() {
        return markEvaluatorResolved.getEnergyScheme().getRegionMap();
    }

    public NamedProviderStore<Stack> getBackgroundStacks() {
        return backgroundStacks;
    }

    public Optional<EvaluatorWithContext> createGuessEvaluator(ToolErrorReporter errorReporter) {
        return EvaluatorFactory.createGuessEvaluator(
                markProposerGuess, markEvaluatorResolved, getRegionMap(), errorReporter);
    }

    public Optional<EvaluatorWithContext> createSelectPointsEvaluator(
            Dimensions dimViewer, ToolErrorReporter errorReporter) {
        return EvaluatorFactory.createSelectPointsEvaluator(
                dimViewer, markEvaluatorResolved, getRegionMap(), errorReporter);
    }

    public PointsFitter getPointsFitterSelectPoints() {
        return pointsFitterSelectPoints;
    }

    private static MarksInitialization setupEvaluatorAndPointsFitter(
            MarkEvaluatorResolved markEvaluator) throws CreateException {
        return markEvaluator.getProposerSharedObjectsOperation().get();
    }

    private static PointsFitter extractPointsFitter(
            MarkProposerStrategy annotationStrategy, MarksInitialization initialization)
            throws CreateException {
        try {
            return initialization
                    .getPoints()
                    .getPointsFitterSet()
                    .getException(annotationStrategy.getPointsFitter());
        } catch (NamedProviderGetException e) {
            throw new CreateException(e);
        }
    }

    private static MarkProposer setupGuess(
            MarksInitialization initialization,
            MarkProposerStrategy annotationStrategy,
            Logger logger) {
        try {
            return initialization
                    .getMarkProposerSet()
                    .getException(annotationStrategy.getMarkProposer());
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
            markEvaluatorSet.add(
                    annotationStrategy.getMarkEvaluator().getName(),
                    annotationStrategy.getMarkEvaluator().getItem());

            return markEvaluatorSet.get(annotationStrategy.getMarkEvaluator().getName());
        } catch (OperationFailedException e1) {
            throw new CreateException(e1);
        }
    }
}
