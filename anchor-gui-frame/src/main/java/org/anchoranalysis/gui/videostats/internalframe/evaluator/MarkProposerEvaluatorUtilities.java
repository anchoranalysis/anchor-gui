/*-
 * #%L
 * anchor-gui-frame
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.mark.ColoredMarks;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkWithPosition;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkConicFactory;
import org.anchoranalysis.anchor.mpp.proposer.visualization.CreateProposalVisualization;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkProposerEvaluatorUtilities {

    public static Mark createMarkFromPosition(
            Point3d position,
            Mark templateMark,
            final RandomNumberGenerator randomNumberGenerator) {

        final Mark me = templateMark.duplicate();

        if (!(me instanceof MarkWithPosition)) {
            throw new IllegalArgumentException("templateMark is not MarkAbstractPosition");
        }

        MarkWithPosition meCast = (MarkWithPosition) me;
        meCast.setPos(position);

        return me;
    }

    public static ColoredMarks generateCfgFromMark(
            Mark mark, Point3d position, MarkProposer markProposer, boolean detailedVisualization) {

        ColoredMarks cfg = new ColoredMarks();

        if (mark != null) {
            cfg.addChangeID(mark, new RGBColor(Color.BLUE));
            addMarkAtMousePoint(position, cfg, mark.numDims() == 3);
        }

        Optional<CreateProposalVisualization> proposalVisualization =
                markProposer.proposalVisualization(detailedVisualization);
        proposalVisualization.ifPresent(pv -> pv.addToCfg(cfg));
        return cfg;
    }

    private static void addMarkAtMousePoint(Point3d position, ColoredMarks cfg, boolean do3D) {
        Mark mousePoint = MarkConicFactory.createMarkFromPoint(position, 1, do3D);
        cfg.addChangeID(mousePoint, new RGBColor(Color.GREEN));
    }
}
