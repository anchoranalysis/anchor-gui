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

package org.anchoranalysis.gui.videostats.internalframe.markredraw;

import java.awt.Color;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;
import org.anchoranalysis.mpp.bean.mark.MarkWithIdentifierFactory;
import org.anchoranalysis.mpp.bean.proposer.MarkCollectionProposer;
import org.anchoranalysis.mpp.mark.ColoredMarks;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.spatial.point.Point3d;

@AllArgsConstructor
public class MarksProposerEvaluator implements ProposalOperationCreator {

    private final MarkCollectionProposer proposer;

    private static ColoredMarks generateOutputMarks(Optional<MarkCollection> marks) {

        if (!marks.isPresent()) {
            return new ColoredMarks();
        }

        MarkCollection marksNew = marks.get().deepCopy();

        // We replace all the IDs with 0
        for (Mark mark : marksNew) {
            mark.setId(0);
        }

        return new ColoredMarks(marksNew, createDefaultColorList());
    }

    @Override
    public ProposalOperation create(
            MarkCollection marks,
            Point3d position,
            ProposerContext context,
            final MarkWithIdentifierFactory markFactory) {
        return errorNode -> {
            ProposedMarks proposal = new ProposedMarks();

            // TODO replace proposer
            Optional<MarkCollection> marksProposed =
                    proposer.propose(markFactory, context.replaceError(errorNode));
            proposal.setSuccess(marksProposed.isPresent());

            if (marksProposed.isPresent()) {

                ColoredMarks coloredMarks = generateOutputMarks(marksProposed);
                proposal.setColoredMarks(coloredMarks);
                proposal.setMarksToRedraw(
                        marksProposed.get().createMerged(coloredMarks.getMarks()));
                proposal.setMarksCore(marksProposed.get());

                proposal.setSuggestedSliceNum((int) marksProposed.get().get(0).centerPoint().z());
            }
            return proposal;
        };
    }

    private static ColorList createDefaultColorList() {
        ColorList colorList = new ColorList();
        colorList.add(new RGBColor(Color.BLUE)); //  0 is the mark added
        colorList.add(new RGBColor(Color.RED)); //  1 is any debug marks
        colorList.add(new RGBColor(Color.GREEN)); //  2 center point
        colorList.add(new RGBColor(Color.YELLOW));
        return colorList;
    }
}
