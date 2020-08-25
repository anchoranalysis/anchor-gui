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

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.bean.proposer.MarkMergeProposer;
import org.anchoranalysis.mpp.mark.ColoredMarks;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.mark.points.PointListFactory;
import org.anchoranalysis.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.mpp.proposer.error.ErrorNode;

@AllArgsConstructor
public class MarkMergeProposerEvaluator implements ProposalOperationCreator {

    private final MarkMergeProposer markMergeProposer;

    @Override
    public ProposalOperation create(
            final MarkCollection marks,
            Point3d position,
            final ProposerContext context,
            final MarkWithIdentifierFactory markFactory)
            throws OperationFailedException {

        if (marks.size() != 2) {
            throw new IllegalArgumentException(
                    "The existing configuration must have exactly 2 items");
        }

        return new ProposalOperation() {
            @Override
            public ProposedMarks propose(ErrorNode errorNode)
                    throws ProposalAbnormalFailureException {

                Mark mark1 = marks.get(0);
                Mark mark2 = marks.get(1);

                VoxelizedMarkMemo markMemo1 = context.create(mark1);
                VoxelizedMarkMemo markMemo2 = context.create(mark2);

                Optional<Mark> proposedMark =
                        markMergeProposer.propose(
                                markMemo1, markMemo2, context.replaceError(errorNode));

                ProposedMarks er = new ProposedMarks(context.dimensions());

                if (proposedMark.isPresent()) {
                    er.setSuccess(true);

                    ColoredMarks coloredMarks = marksForMark(proposedMark);
                    er.setColoredMarks(coloredMarks);
                    er.setMarksToRedraw(marks.createMerged(coloredMarks.getMarks()));
                    er.setMarksCore(new MarkCollection(proposedMark.get()));
                } else {
                    er.setMarksToRedraw(marks);
                }
                return er;
            }
        };
    }

    private ColoredMarks marksForMark(Optional<Mark> mark) {

        ColoredMarks marksOut = new ColoredMarks();
        if (mark.isPresent()) {

            Mark markNew = mark.get().duplicate();
            markNew.setId(0);

            marksOut.addChangeID(markNew, new RGBColor(Color.BLUE));
        }

        // Allows us to associate a list of points with  the mark
        addToOut(markMergeProposer.getLastPoints1(), Color.GREEN, marksOut);

        // Allows us to associate a list of points with  the mark
        addToOut(markMergeProposer.getLastPoints2(), Color.YELLOW, marksOut);

        return marksOut;
    }

    private static void addToOut(
            Optional<List<Point3f>> points, Color color, ColoredMarks marksOut) {
        if (points.isPresent()) {
            marksOut.addChangeID(
                    PointListFactory.createMarkFromPoints3f(points.get()),
                    new RGBColor(color)); // 1 is just to give us a different color
        }
    }
}
