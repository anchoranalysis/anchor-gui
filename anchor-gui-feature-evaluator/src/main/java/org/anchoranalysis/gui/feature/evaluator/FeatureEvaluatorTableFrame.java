/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator;

import javax.swing.SwingUtilities;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.image.OverlaysWithEnergyStack;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.marks.StatePanelFrame;
import org.anchoranalysis.gui.marks.StatePanelUpdateException;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

public class FeatureEvaluatorTableFrame {

    private StatePanelFrame<OverlaysWithEnergyStack> delegate;

    private ErrorReporter errorReporter;
    private FeatureListSrc featureListSrc;

    public FeatureEvaluatorTableFrame(
            DefaultModuleState defaultFrameState,
            FeatureListSrc featureListSrc,
            boolean defaultKeepLastValid,
            Logger logger)
            throws StatePanelUpdateException {
        this.featureListSrc = featureListSrc;
        delegate =
                new StatePanelFrame<>(
                        "Feature Evaluator",
                        defaultFrameState.getLinkState().getOverlaysWithStack(),
                        new FeatureEvaluatorTablePanel(
                                featureListSrc, defaultKeepLastValid, logger));
        delegate.controllerSize().configureSize(200, 200, 650, 800);
        this.errorReporter = logger.errorReporter();
    }

    public IModuleCreatorDefaultState moduleCreator() {
        return defaultFrameState -> {
            VideoStatsModule module =
                    delegate.moduleCreator().createVideoStatsModule(defaultFrameState);

            LinkModules link = new LinkModules(module);
            link.getOverlaysWithStack().add((value, adjusting) -> showIncomingState(value));

            return module;
        };
    }

    private void showIncomingState(final OverlaysWithEnergyStack value) {
        SwingUtilities.invokeLater(
                () -> {
                    try {
                        delegate.updateState(maybeAugmentParams(value));

                    } catch (StatePanelUpdateException | OperationFailedException e) {
                        errorReporter.recordError(FeatureEvaluatorTableFrame.class, e);
                    }
                });
    }

    private OverlaysWithEnergyStack maybeAugmentParams(OverlaysWithEnergyStack oc)
            throws OperationFailedException {
        return oc.copyChangeStack(featureListSrc.maybeAugmentParams(oc.getStack()));
    }

    public ControllerSize controllerSize() {
        return delegate.controllerSize();
    }
}
