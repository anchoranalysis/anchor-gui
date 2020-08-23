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

package org.anchoranalysis.gui.videostats.internalframe.annotator.tool;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IAcceptProposal;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IReplaceRemove;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;

@AllArgsConstructor
public class GuessTool extends AnnotationTool {

    private IReplaceRemove replaceRemove;
    private IAcceptProposal acceptProposal;
    private Optional<EvaluatorWithContext> evaluatorWithContext;
    private ToolErrorReporter toolErrorReporter;

    public boolean isEnabled() {
        return evaluatorWithContext.isPresent();
    }

    @Override
    public void proposed(ProposedMarks proposedCfg) {

        if (!isEnabled()) {
            return;
        }

        if (proposedCfg.isSuccess()) {
            replaceRemove.replaceCurrentProposedCfg(
                    proposedCfg.getMarksCore(),
                    OverlayCollectionMarkFactory.cfgFromOverlays(proposedCfg.getColoredCfg()),
                    proposedCfg.getSuggestedSliceNum());
        } else {
            replaceRemove.removeCurrentProposedCfg();
            toolErrorReporter.showError(
                    GuessTool.class,
                    "Guess failed. Try again (or select points)!",
                    proposedCfg.getPfd().describe());
        }
    }

    @Override
    public void confirm(boolean accepted) {

        if (!isEnabled()) {
            return;
        }

        acceptProposal.confirm(accepted);
    }

    @Override
    public Optional<EvaluatorWithContext> evaluatorWithContextGetter() {
        return evaluatorWithContext;
    }

    @Override
    public void leftMouseClickedAtPoint(Point3d point) {
        // NOTHING TO DO
    }
}
