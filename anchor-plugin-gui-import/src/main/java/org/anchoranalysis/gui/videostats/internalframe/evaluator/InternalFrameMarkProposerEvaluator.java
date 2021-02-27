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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.marks.proposer.MarksProposerMouseClickAdapter;
import org.anchoranalysis.gui.frame.overlays.IShowEvaluationResult;
import org.anchoranalysis.gui.frame.overlays.IShowOverlays;
import org.anchoranalysis.gui.frame.overlays.InternalFrameOverlaysRedraw;
import org.anchoranalysis.gui.frame.overlays.ProposedMarks;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.kernel.ProposerFailureDescriptionPanel;
import org.anchoranalysis.gui.marks.StatePanelUpdateException;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultStateSliderState;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.OutputPanel;
import org.anchoranalysis.gui.videostats.internalframe.ProposeLoopPanel;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.RedrawUpdateFromProposal;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.FromMarkCollectionProposer;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.FromMarkMergeProposer;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.FromMarkProposer;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.FromMarkSplitProposer;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.fromproposer.ProposalOperationCreatorFromProposer;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.mpp.bean.init.MarksInitialization;
import org.anchoranalysis.mpp.io.marks.MarksWithDisplayStack;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;

public class InternalFrameMarkProposerEvaluator {

    private InternalFrameOverlaysRedraw delegate;

    private EvaluatorChooser evaluatorChooser;
    private ProposeLoopPanel proposeLoopPanel;
    private HistoryNavigator historyNavigator;
    private OutputPanel outputPanel;
    private ErrorReporter errorReporter;

    private ProposerFailureDescriptionPanel failurePanel;

    public InternalFrameMarkProposerEvaluator(ErrorReporter errorReporter) {

        this.outputPanel = new OutputPanel();
        this.proposeLoopPanel = new ProposeLoopPanel();
        this.errorReporter = errorReporter;
        evaluatorChooser =
                new EvaluatorChooser(generateEvaluators(proposeLoopPanel), errorReporter);

        delegate = new InternalFrameOverlaysRedraw("Proposer Evaluator");

        this.failurePanel = new ProposerFailureDescriptionPanel(delegate.getDelegate());
    }

    public void addMarkEvaluatorChangedListener(MarkEvaluatorChangedListener listener) {
        evaluatorChooser.addMarkEvaluatorChangedListener(listener);
    }

    public SliderState init(
            MarkEvaluatorSetForImage markEvaluatorSet,
            DefaultModuleState defaultState,
            OutputWriteSettings outputWriteSettings,
            VideoStatsModuleGlobalParams mpg)
            throws InitException {

        evaluatorChooser.init(markEvaluatorSet);

        outputPanel.init(
                mpg.getDefaultColorIndexForMarks(), mpg.getExportPopupParams().getContext());

        SliderState sliderState = delegate.init(defaultState, mpg);

        setupHistoryNavigator(sliderState);

        delegate.controllerAction().order().setAsBottomComponent(createBottomPanel());

        MarksProposerMouseClickAdapter clickListener =
                new MarksProposerMouseClickAdapter(
                        delegate.extractOverlays(),
                        sliderState,
                        evaluatorChooser.evaluatorWithContext(),
                        mpg.getRandomNumberGenerator(),
                        mpg.getLogger().errorReporter());
        delegate.controllerAction().mouse().addMouseListener(clickListener, false);

        final AddToHistoryNavigator showEvaluationResult = new AddToHistoryNavigator();

        clickListener.addMarksProposedListener(
                proposedMarks -> showEvaluationResult.showEvaluationResult(proposedMarks, null));
        return sliderState;
    }

    private void setupHistoryNavigator(SliderState sliderState) {
        IShowOverlays marksShower = delegate.showOverlays(sliderState);
        this.historyNavigator =
                new HistoryNavigator(new ShowEvaluationResultFromHistoryNavigator(marksShower));
    }

    /** A panel which incorporates both our failure dialog and options */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(failurePanel.getPanel(), BorderLayout.CENTER);
        bottomPanel.add(createBotttomMultipleLines(), BorderLayout.SOUTH);
        return bottomPanel;
    }

    private JPanel createBotttomMultipleLines() {
        JPanel bottomMultipleLines = new JPanel();
        bottomMultipleLines.setLayout(new GridLayout(4, 1));
        bottomMultipleLines.add(proposeLoopPanel.getPanel());
        bottomMultipleLines.add(outputPanel.getPanel());
        bottomMultipleLines.add(historyNavigator.getPanel());
        bottomMultipleLines.add(evaluatorChooser.getPanel());
        return bottomMultipleLines;
    }

    private class AddToHistoryNavigator implements IShowEvaluationResult {

        @Override
        public void showEvaluationResult(ProposedMarks er, MarkCollection boxRedraw) {
            historyNavigator.add(er);
        }
    }

    private class ShowEvaluationResultFromHistoryNavigator implements IShowEvaluationResult {

        private IShowOverlays showResult;

        public ShowEvaluationResultFromHistoryNavigator(IShowOverlays showResult) {
            super();
            this.showResult = showResult;
        }

        @Override
        public void showEvaluationResult(ProposedMarks er, MarkCollection boxRedraw) {

            try {
                // We the marks back from the overlays
                MarkCollection marks =
                        OverlayCollectionMarkFactory.marksFromOverlays(
                                er.getColoredMarks().getOverlays());

                outputPanel.output(new MarksWithDisplayStack(marks, delegate.getBackground()));

                showResult.showOverlays(RedrawUpdateFromProposal.apply(er, null));

                failurePanel.updateState(er.getPfd());

            } catch (StatePanelUpdateException e) {
                errorReporter.recordError(InternalFrameOverlaysRedraw.class, e);
            }
        }
    }

    private static <T> ProposalOperationCreatorFromProposer<T> wrapInLoop(
            final ProposalOperationCreatorFromProposer<T> creator,
            final ProposeLoopPanel proposeLoopPanel) {

        return new ProposalOperationCreatorFromProposer<T>() {

            @Override
            public ProposalOperationCreator creatorFromProposer(T proposer) {
                ProposalOperationCreator poc = creator.creatorFromProposer(proposer);
                return new AddLoop(poc, proposeLoopPanel);
            }

            @Override
            public NamedProvider<T> allProposers(MarksInitialization so) {
                return creator.allProposers(so);
            }

            @Override
            public String getEvaluatorName() {
                return creator.getEvaluatorName();
            }
        };
    }

    public static List<ProposalOperationCreatorFromProposer<?>> generateEvaluators(
            ProposeLoopPanel proposeLoopPanel) {

        List<ProposalOperationCreatorFromProposer<?>> listEvaluators = new ArrayList<>();
        listEvaluators.add(new FromMarkProposer());
        listEvaluators.add(new FromMarkSplitProposer());
        listEvaluators.add(new FromMarkMergeProposer());
        listEvaluators.add(new FromMarkCollectionProposer());

        List<ProposalOperationCreatorFromProposer<?>> listWithLoop = new ArrayList<>();
        for (ProposalOperationCreatorFromProposer<?> item : listEvaluators) {
            listWithLoop.add(wrapInLoop(item, proposeLoopPanel));
        }
        return listWithLoop;
    }

    public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
        return delegate.controllerBackgroundMenu();
    }

    public IModuleCreatorDefaultStateSliderState moduleCreator() {
        return delegate.moduleCreator();
    }

    public ControllerImageView controllerImageView() {
        return delegate.controllerImageView();
    }
}
