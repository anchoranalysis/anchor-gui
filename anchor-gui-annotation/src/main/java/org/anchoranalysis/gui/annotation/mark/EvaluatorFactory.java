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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorResolved;
import org.anchoranalysis.gui.videostats.internalframe.annotator.InternalFrameAnnotator;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkProposerEvaluatorDimensions;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.MarkSphereOnPointProposerEvaluator;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;

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
                            markEvaluatorResolved.getEnergyStack(),
                            markEvaluatorResolved.getMarkFactory(),
                            regionMap));
        } catch (OperationFailedException e) {
            errorReporter.showError(
                    InternalFrameAnnotator.class, "Cannot create guess evaluator", e.toString());
            return Optional.empty();
        }
    }

    public static Optional<EvaluatorWithContext> createSelectPointsEvaluator(
            Dimensions dimViewer,
            MarkEvaluatorResolved markEvaluatorResolved,
            RegionMap regionMap,
            ToolErrorReporter errorReporter) {
        try {
            return Optional.of(
                    new EvaluatorWithContext(
                            new MarkSphereOnPointProposerEvaluator(dimViewer),
                            markEvaluatorResolved.getEnergyStack(),
                            markEvaluatorResolved.getMarkFactory(),
                            regionMap));
        } catch (OperationFailedException e) {
            errorReporter.showError(
                    InternalFrameAnnotator.class,
                    "Cannot create select-points evaluator",
                    e.toString());
            return Optional.empty();
        }
    }
}
