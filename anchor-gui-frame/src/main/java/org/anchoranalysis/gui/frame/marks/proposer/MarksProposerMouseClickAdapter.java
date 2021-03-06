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

package org.anchoranalysis.gui.frame.marks.proposer;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.event.EventListenerList;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.frame.overlays.ExtractOverlays;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContextGetter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.mpp.proposer.ProposerContext;
import org.anchoranalysis.mpp.proposer.error.ErrorNodeNull;
import org.anchoranalysis.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.spatial.point.Point3d;

@RequiredArgsConstructor
public class MarksProposerMouseClickAdapter extends MouseAdapter {

    // START REQUIRED ARGUMENTS
    private final ExtractOverlays extractOverlays;
    private final SliderState sliderState;
    private final EvaluatorWithContextGetter evaluatorGetter;
    private final RandomNumberGenerator randomNumberGenerator;
    private final ErrorReporter errorReporter;
    // END REQUIRED ARGUMENTS

    private EventListenerList eventListeners = new EventListenerList();

    @Override
    public void mouseReleased(MouseEvent arg0) {
        super.mouseReleased(arg0);

        if (arg0.isControlDown() || arg0.isShiftDown() || arg0.isMetaDown()) {
            return;
        }

        int modifiers = arg0.getModifiers();
        if ((modifiers & InputEvent.BUTTON1_MASK) != InputEvent.BUTTON1_MASK) {
            // If it's not the left mouse button, we ignore it
            return;
        }

        Optional<EvaluatorWithContext> evaluatorWithContext;
        try {
            evaluatorWithContext = evaluatorGetter.getEvaluatorWithContext();
            if (!evaluatorWithContext.isPresent()) {
                // If we have no evaluatorWithCotnext we ignored it
                return;
            }
        } catch (OperationFailedException e) {
            errorReporter.recordError(MarksProposerMouseClickAdapter.class, e);
            return;
        }

        try {
            addMarks(
                    new Point3d(arg0.getX(), arg0.getY(), sliderState.getSliceNum()),
                    evaluatorWithContext.get());
        } catch (ProposalAbnormalFailureException e) {

            errorReporter.recordErrorFormatted(
                    MarksProposerMouseClickAdapter.class,
                    "Failed to propose marks due to an abnormal error%n%s%n",
                    e.friendlyMessageHierarchy());
        }
    }

    private void addMarks(Point3d position, EvaluatorWithContext evaluatorWithContext)
            throws ProposalAbnormalFailureException {

        // We exit early if position is outside our scene size
        if (!extractOverlays.dimensions().contains(position)) {
            return;
        }

        // We convert the overlays into a Marks. There's almost definitely a better way of doing
        // this
        MarkCollection marks =
                OverlayCollectionMarkFactory.marksFromOverlays(
                        extractOverlays.getOverlays().getOverlays());

        ProposedMarks er = generateEvaluationResult(marks, position, evaluatorWithContext);

        for (ProposedMarksListener al : eventListeners.getListeners(ProposedMarksListener.class)) {
            al.proposed(er);
        }
    }

    private ProposedMarks generateEvaluationResult(
            MarkCollection marksExst, Point3d position, EvaluatorWithContext evaluatorWithContext)
            throws ProposalAbnormalFailureException {

        ProposalOperationCreator evaluator = evaluatorWithContext.getEvaluator();

        if (evaluator != null) {

            ProposerFailureDescription pfd = ProposerFailureDescription.createRoot();

            try {
                ProposerContext context =
                        new ProposerContext(
                                randomNumberGenerator,
                                evaluatorWithContext.getEnergyStack(),
                                evaluatorWithContext.getRegionMap(),
                                ErrorNodeNull.instance());

                final ProposalOperation proposalOperation =
                        evaluator.create(
                                marksExst,
                                position,
                                context,
                                evaluatorWithContext.getMarkFactory());
                ProposedMarks er = proposalOperation.propose(pfd.getRoot());
                er.setPfd(pfd);
                return er;
            } catch (OperationFailedException e) {
                ProposedMarks er = new ProposedMarks();
                er.setSuccess(false);
                pfd.getRoot().add(e);
                er.setPfd(pfd);
                return er;
            }

        } else {
            return new ProposedMarks();
        }
    }

    public void addMarksProposedListener(ProposedMarksListener a) {
        eventListeners.add(ProposedMarksListener.class, a);
    }
}
