/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.gui.annotation.mark.MarkAnnotator;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.InitParamsProposeMarks;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.ISaveActionListenerFactory;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.CurrentStateDisplayer;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelAnnotationIO;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelMark;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelTool;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.extent.ImageDimensions;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperPanelBuilder {

    public static PanelNavigation createPanelNavigation(
            CurrentStateDisplayer currentStateDisplayer,
            InitParamsProposeMarks paramsInit,
            AnnotationPanelParams params,
            AnnotationFrameControllers controllers,
            ISaveActionListenerFactory saveActions) {
        PanelAnnotationIO panelSave = new PanelAnnotationIO();
        panelSave.addActionsToSavePanel(saveActions, controllers.action().frame().getFrame());

        PanelTool panelTool =
                createPanelTool(
                        currentStateDisplayer,
                        paramsInit.getDimensionsViewer(),
                        paramsInit.getMarkAnnotator(),
                        params.getErrorReporter());

        PanelMark panelMark =
                new PanelMark(
                        currentStateDisplayer.confirmReset(),
                        currentStateDisplayer.undoRedo(),
                        panelTool);

        PanelNavigation panelNavigation =
                new PanelNavigation(panelTool.getPanel(), panelMark, panelSave.getPanel());

        controllers.popup().addAdditionalMenu(panelTool.createSwitchToolMenu());

        HelperAddActions.apply(
                controllers.action(), controllers.extractOverlays(), params, panelTool, panelMark);

        return panelNavigation;
    }

    private static PanelTool createPanelTool(
            CurrentStateDisplayer currentStateDisplayer,
            ImageDimensions dimViewer,
            MarkAnnotator markAnnotator,
            ToolErrorReporter errorReporter) {
        Optional<EvaluatorWithContext> guessEvaluator =
                markAnnotator.createGuessEvaluator(errorReporter);

        Optional<EvaluatorWithContext> evaluatorSelectPoints =
                markAnnotator.createSelectPointsEvaluator(dimViewer, errorReporter);

        return new PanelTool(
                currentStateDisplayer,
                guessEvaluator,
                evaluatorSelectPoints,
                markAnnotator.getPointsFitterSelectPoints(),
                errorReporter);
    }
}
