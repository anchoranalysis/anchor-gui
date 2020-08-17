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

package org.anchoranalysis.gui.videostats.dropdown.common;

import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.BackgroundUpdater;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.IUpdatableMarkEvaluator;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.InternalFrameMarkProposerEvaluator;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreator;
import org.anchoranalysis.image.bean.nonbean.init.CreateCombinedStack;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.stack.wrap.WrapStackAsTimeSequence;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

@AllArgsConstructor
class ProposerEvaluatorModuleCreator extends VideoStatsModuleCreator {

    private MarkEvaluatorSetForImage markEvaluatorSet;
    private NRGBackground nrgBackground;
    private OutputWriteSettings outputWriteSettings;
    private IUpdatableMarkEvaluator markEvaluatorUpdater;
    private VideoStatsModuleGlobalParams mpg;

    @Override
    public void createAndAddVideoStatsModule(AddVideoStatsModule adder)
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
                            outputWriteSettings,
                            mpg);

            BackgroundUpdater backgroundUpdater =
                    imageFrame
                            .controllerBackgroundMenu()
                            .add(mpg, nrgBackground.getBackgroundSet());

            imageFrame.addMarkEvaluatorChangedListener(
                    e -> {
                        if (e.getMarkEvaluator() != null) {
                            backgroundUpdater.update(
                                    BackgroundSetProgressingSupplier.cache(
                                            progressReporter ->
                                                    createBackgroundSetFromExisting(
                                                            progressReporter,
                                                            e.getMarkEvaluator()
                                                                    .getProposerSharedObjectsOperation())));
                            markEvaluatorUpdater.setMarkEvaluatorIdentifier(
                                    e.getMarkEvaluatorName());
                        } else {

                            backgroundUpdater.update(nrgBackground.getBackgroundSet());
                            markEvaluatorUpdater.setMarkEvaluatorIdentifier(null);
                        }
                    });

            ModuleAddUtilities.add(adder, imageFrame.moduleCreator(), sliderState);

        } catch (VideoStatsModuleCreateException | InitException e) {
            mpg.getLogger().errorReporter().recordError(ProposerEvaluatorModuleCreator.class, e);
        }
    }

    private BackgroundSet createBackgroundSetFromExisting(
            ProgressReporter progressReporter, CachedSupplier<MPPInitParams, CreateException> pso)
            throws BackgroundStackContainerException {

        try {
            BackgroundSet bsExisting = nrgBackground.getBackgroundSet().get(progressReporter);

            MPPInitParams so = pso.get();
            ImageInitParams soImage = so.getImage();

            return BackgroundSetFactory.createBackgroundSetFromExisting(
                    bsExisting,
                    new WrapStackAsTimeSequence(CreateCombinedStack.apply(soImage)),
                    progressReporter);

        } catch (CreateException e) {
            throw new BackgroundStackContainerException(e);
        }
    }
}
