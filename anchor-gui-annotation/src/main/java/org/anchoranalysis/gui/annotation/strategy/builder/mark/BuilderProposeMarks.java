/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.annotation.builder.AdditionalFramesContext;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilderWithDelegate;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiContext;
import org.anchoranalysis.gui.annotation.export.ExportAnnotation;
import org.anchoranalysis.gui.annotation.export.ExportScaledMarks;
import org.anchoranalysis.gui.annotation.mark.MarkAnnotator;
import org.anchoranalysis.gui.annotation.opener.OpenAnnotationMPP;
import org.anchoranalysis.gui.annotation.state.AnnotationSummary;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.panel.CreateNavigationPanel;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;
import org.anchoranalysis.plugin.annotation.bean.strategy.PathFromGenerator;

public class BuilderProposeMarks
        extends AnnotationGuiBuilderWithDelegate<InitParamsProposeMarks, MarkProposerStrategy> {

    private MarkAnnotationReader annotationReader = new MarkAnnotationReader(true);

    private OpenAnnotationMPP openAnnotation;

    public BuilderProposeMarks(AnnotationWithStrategy<MarkProposerStrategy> delegate)
            throws AnchorIOException {
        super(delegate);
        this.openAnnotation = createOpenAnnotation();
    }

    private OpenAnnotationMPP createOpenAnnotation() throws AnchorIOException {

        Optional<Path> cfgPath =
                OptionalUtilities.mapBoth(
                        getStrategy().cfgFilePathGenerator(),
                        pathForBinding(),
                        PathFromGenerator::derivePath);

        return new OpenAnnotationMPP(annotationPath(), cfgPath, annotationReader);
    }

    @Override
    public InitParamsProposeMarks createInitParams(
            ProgressReporterMultiple prm,
            AnnotationGuiContext context,
            Logger logger,
            boolean useDefaultCfg)
            throws CreateException {

        try {
            MarkAnnotator markAnnotator =
                    CreateMarkEvaluator.apply(
                            context.getMarkEvaluatorManager(),
                            pathForBindingRequired(),
                            getStrategy(),
                            stacks(),
                            logger);

            // Open initial cfg
            InitAnnotation annotationExst = openAnnotation.open(useDefaultCfg, logger);

            return new InitParamsProposeMarks(
                    context.getAnnotationRefresher(),
                    markAnnotator,
                    createBackground(prm, markAnnotator.getBackgroundStacks()),
                    annotationExst);

        } catch (VideoStatsModuleCreateException | AnchorIOException e) {
            throw new CreateException(e);
        }
    }

    @Override
    public PanelNavigation createInitPanelNavigation(
            InitParamsProposeMarks paramsInit,
            AnnotationPanelParams params,
            AnnotationFrameControllers controllers) {
        return CreateNavigationPanel.apply(paramsInit, params, controllers, annotationPath());
    }

    @Override
    public void showAllAdditional(
            InitParamsProposeMarks paramsInit, AdditionalFramesContext context)
            throws OperationFailedException {

        try {
            ShowAdditionalFrames.apply(
                    paramsInit, context, pathForBindingRequired(), getStrategy());
        } catch (AnchorIOException e) {
            throw new OperationFailedException(e);
        }
    }

    /** Provides for an interactive GUI export of an annotation */
    @Override
    public ExportAnnotation exportAnnotation() {
        return new ExportScaledMarks(annotationPath());
    }

    @Override
    public boolean isUseDefaultPromptNeeded() {
        return openAnnotation.isUseDefaultPromptNeeded();
    }

    @Override
    public int heightNonImagePanel() {
        return 50;
    }

    @Override
    protected AnnotationSummary createSummary() throws CreateException {
        return CreateAnnotationSummary.apply(annotationPath(), annotationReader);
    }
}
