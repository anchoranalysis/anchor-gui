/* (C)2020 */
package org.anchoranalysis.gui.videostats.dropdown.common;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IBackgroundUpdater;
import org.anchoranalysis.gui.videostats.dropdown.CreateBackgroundSetFromExisting;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.IUpdatableMarkEvaluator;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.InternalFrameMarkProposerEvaluator;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

class ProposerEvaluatorModuleCreator extends VideoStatsModuleCreator {

    private MarkEvaluatorSetForImage markEvaluatorSet;
    private NRGBackground nrgBackground;
    private OutputWriteSettings outputWriteSettings;
    private IUpdatableMarkEvaluator markEvaluatorUpdater;
    private VideoStatsModuleGlobalParams mpg;

    public ProposerEvaluatorModuleCreator(
            MarkEvaluatorSetForImage markEvaluatorSet,
            NRGBackground nrgBackground,
            OutputWriteSettings outputWriteSettings,
            IUpdatableMarkEvaluator markEvaluatorUpdater,
            VideoStatsModuleGlobalParams mpg) {
        super();
        this.markEvaluatorSet = markEvaluatorSet;
        this.nrgBackground = nrgBackground;
        this.outputWriteSettings = outputWriteSettings;
        this.mpg = mpg;
        this.markEvaluatorUpdater = markEvaluatorUpdater;
    }

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            InternalFrameMarkProposerEvaluator imageFrame =
                    new InternalFrameMarkProposerEvaluator(mpg.getLogger().errorReporter());

            // Configure initial-size based upon overall window size
            imageFrame
                    .controllerImageView()
                    .configure(0.8, 0.6, 0, 50, mpg.getGraphicsCurrentScreen());

            // Here we optionally set an adder to send back nrg_stacks
            ISliderState sliderState =
                    imageFrame.init(
                            markEvaluatorSet,
                            adder.getSubgroup().getDefaultModuleState().getState(),
                            nrgBackground.getBackgroundSet(),
                            outputWriteSettings,
                            mpg);

            IBackgroundUpdater backgroundUpdater =
                    imageFrame
                            .controllerBackgroundMenu()
                            .add(mpg, nrgBackground.getBackgroundSet());

            imageFrame.addMarkEvaluatorChangedListener(
                    e -> {
                        if (e.getMarkEvaluator() != null) {
                            backgroundUpdater.update(
                                    new CreateBackgroundSetFromExisting(
                                            nrgBackground.getBackgroundSet(),
                                            e.getMarkEvaluator()
                                                    .getProposerSharedObjectsOperation(),
                                            outputWriteSettings));
                            markEvaluatorUpdater.setMarkEvaluatorIdentifier(
                                    e.getMarkEvaluatorName());
                        } else {

                            backgroundUpdater.update(nrgBackground.getBackgroundSet());
                            markEvaluatorUpdater.setMarkEvaluatorIdentifier(null);
                        }
                    });

            ModuleAddUtilities.add(adder, imageFrame.moduleCreator(), sliderState);

        } catch (VideoStatsModuleCreateException e) {
            mpg.getLogger().errorReporter().recordError(ProposerEvaluatorModuleCreator.class, e);
        } catch (InitException e) {
            mpg.getLogger().errorReporter().recordError(ProposerEvaluatorModuleCreator.class, e);
        }
    }
}
