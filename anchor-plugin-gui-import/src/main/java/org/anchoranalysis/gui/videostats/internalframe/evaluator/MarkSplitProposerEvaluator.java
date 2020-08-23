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
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.cfg.MarkWithIdentifierFactory;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkSplitProposer;
import org.anchoranalysis.anchor.mpp.mark.ColoredMarks;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.anchor.mpp.pair.PairPxlMarkMemo;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;

public class MarkSplitProposerEvaluator implements ProposalOperationCreator {

    private final MarkSplitProposer markSplitProposer;

    private Optional<PairPxlMarkMemo> pair = Optional.empty();
    private Mark exstMark;

    @SuppressWarnings("unused")
    private MarkCollection exstCfg;

    public MarkSplitProposerEvaluator(MarkSplitProposer markSplitProposer) {
        super();
        assert (markSplitProposer != null);

        this.markSplitProposer = markSplitProposer;
    }

    @Override
    public ProposalOperation create(
            final MarkCollection cfg, Point3d position, final ProposerContext context, final MarkWithIdentifierFactory markFactory)
            throws OperationFailedException {

        this.exstCfg = cfg;

        // We need to get the mark already at this position
        final MarkCollection marksAtPost =
                cfg.marksAt(
                        position, context.getRegionMap(), GlobalRegionIdentifiers.SUBMARK_INSIDE);

        return new ProposalOperation() {

            @Override
            public ProposedMarks propose(ErrorNode errorNode)
                    throws ProposalAbnormalFailureException {

                if (marksAtPost.size() == 0) {
                    errorNode.add("no existing mark found");
                    return new ProposedMarks();
                }

                if (marksAtPost.size() > 1) {
                    errorNode.add("more than one existing mark found");
                    return new ProposedMarks();
                }

                exstMark = marksAtPost.get(0);

                {
                    VoxelizedMarkMemo pmmExstMark = context.create(exstMark);
                    pair = markSplitProposer.propose(pmmExstMark, context, markFactory);
                }

                if (pair.isPresent()) {
                    ProposedMarks er = new ProposedMarks(context.dimensions());
                    er.setSuccess(true);
                    er.setColoredCfg(cfgForLast());
                    er.setMarksToRedraw(cfg);

                    MarkCollection core = new MarkCollection();
                    core.add(pair.get().getSource().getMark());
                    core.add(pair.get().getDestination().getMark());
                    er.setMarksCore(core);
                    return er;
                } else {
                    ProposedMarks er = new ProposedMarks();
                    er.setMarksToRedraw(cfg);
                    return er;
                }
            }
        };
    }

    private ColoredMarks cfgForLast() {
        ColoredMarks cfgOut = new ColoredMarks();
        if (pair.isPresent()) {
            // We change the IDs
            cfgOut.addChangeID(pair.get().getSource().getMark(), new RGBColor(Color.BLUE));
            cfgOut.addChangeID(pair.get().getDestination().getMark(), new RGBColor(Color.RED));
        }
        return cfgOut;
    }
}
