/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator;

import javax.swing.SwingUtilities;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.gui.cfgnrg.StatePanelFrame;
import org.anchoranalysis.gui.cfgnrg.StatePanelUpdateException;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;
import org.anchoranalysis.gui.image.frame.ControllerSize;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultState;
import org.anchoranalysis.gui.videostats.link.LinkModules;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;

public class FeatureEvaluatorTableFrame {

    private StatePanelFrame<OverlayCollectionWithImgStack> delegate;

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
                        defaultFrameState.getLinkState().getCfgWithStack(),
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

    private void showIncomingState(final OverlayCollectionWithImgStack value) {
        SwingUtilities.invokeLater(
                () -> {
                    try {
                        delegate.updateState(maybeAugmentParams(value));

                    } catch (StatePanelUpdateException | OperationFailedException e) {
                        errorReporter.recordError(FeatureEvaluatorTableFrame.class, e);
                    }
                });
    }

    private OverlayCollectionWithImgStack maybeAugmentParams(OverlayCollectionWithImgStack oc)
            throws OperationFailedException {
        return oc.copyChangeStack(featureListSrc.maybeAugmentParams(oc.getStack()));
    }

    public ControllerSize controllerSize() {
        return delegate.controllerSize();
    }
}
