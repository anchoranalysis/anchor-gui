/*-
 * #%L
 * anchor-plugin-gui-import
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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.spatial.point.Point3d;

@AllArgsConstructor
public class MarkProposerEvaluatorEnergyStack implements ProposalOperationCreator {

    private MarkProposer markProposer;
    private boolean detailedVisualization;

    @Override
    public ProposalOperation create(
            MarkCollection marks,
            final Point3d position,
            final ProposerContext context,
            final MarkWithIdentifierFactory markFactory)
            throws OperationFailedException {

        final Mark mark =
                MarkProposerEvaluatorUtilities.createMarkFromPosition(
                        position,
                        markFactory.getTemplateMark().create(),
                        context.getRandomNumberGenerator());

        // Do proposal
        return errorNode -> {
            VoxelizedMarkMemo pmm = context.create(mark);

            ProposedMarks proposal = new ProposedMarks(context.dimensions());

            // assumes only called once
            boolean success = markProposer.propose(pmm, context);

            proposal.setSuccess(success);

            if (success) {
                proposal.setColoredMarks(
                        MarkProposerEvaluatorUtilities.generateMarksFromMark(
                                pmm.getMark(), position, markProposer, detailedVisualization));

                proposal.setSuggestedSliceNum((int) mark.centerPoint().z());
                proposal.setMarksCore(new MarkCollection(pmm.getMark()));
            }

            return proposal;
        };
    }
}
