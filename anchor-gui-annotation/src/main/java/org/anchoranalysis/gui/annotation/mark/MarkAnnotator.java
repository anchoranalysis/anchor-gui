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
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.extent.Dimensions;
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

        this.backgroundStacks = soMPP.getImage().stacks();
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

    private static MPPInitParams setupEvaluatorAndPointsFitter(
            MarkEvaluatorResolved markEvaluator, MarkProposerStrategy annotationStrategy)
            throws CreateException {
        return markEvaluator.getProposerSharedObjectsOperation().get();
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
        } catch (OperationFailedException e1) {
            throw new CreateException(e1);
        }
    }
}
