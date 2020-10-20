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

package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.PxlMarkMemoFactory;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.spatial.point.Point3d;

@RequiredArgsConstructor
public class MarkProposerEvaluatorDimensions implements ProposalOperationCreator {

    // START REQUIRED ARGUMENTS
    private final MarkProposer markProposer;
    private final boolean detailedVisualization;
    // END REQUIRED ARGUMENTS

    private Dimensions dimensions;

    @Override
    public ProposalOperation create(
            MarkCollection marks,
            final Point3d position,
            final ProposerContext context,
            final MarkWithIdentifierFactory markFactory)
            throws OperationFailedException {

        // We actually do the proposal
        final Mark mark =
                MarkProposerEvaluatorUtilities.createMarkFromPosition(
                        position,
                        markFactory.getTemplateMark().create(),
                        context.getRandomNumberGenerator());

        // Do proposal
        return errorNode -> {
            VoxelizedMarkMemo pmm = PxlMarkMemoFactory.create(mark, null, context.getRegionMap());

            ProposedMarks er = new ProposedMarks(dimensions);

            assert (markProposer.isInitialized());

            // assumes only called once
            boolean succ = markProposer.propose(pmm, context);

            er.setSuccess(succ);

            if (succ) {
                er.setColoredMarks(
                        MarkProposerEvaluatorUtilities.generateMarksFromMark(
                                pmm.getMark(), position, markProposer, detailedVisualization));
                er.setSuggestedSliceNum((int) mark.centerPoint().z());
                er.setMarksCore(new MarkCollection(pmm.getMark()));
            }

            return er;
        };
    }
}
