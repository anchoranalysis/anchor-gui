/* (C)2020 */
package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.InitParamsProposeMarks;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.ISaveActionListenerFactory;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveActionListenerFactory;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.CurrentStateDisplayer;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.IQueryAcceptedRejected;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.ShowCurrentState;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;

public class CreateNavigationPanel {

    public static PanelNavigation apply(
            InitParamsProposeMarks paramsInit,
            AnnotationPanelParams params,
            AnnotationFrameControllers controllers,
            Path annotationPath) {
        CurrentStateDisplayer currentStateDisplayer =
                new CurrentStateDisplayer(
                        new ShowCurrentState(controllers.showOverlays(), params.getErrorReporter()),
                        params.getSaveMonitor(),
                        paramsInit.getDimensionsViewer(),
                        paramsInit
                                .getMarkAnnotator()
                                .getRegionMap(), // How we calculate the overlap
                        params.getErrorReporter());

        ISaveActionListenerFactory saveActions =
                createSaveActions(
                        annotationPath,
                        currentStateDisplayer.queryAcceptReject(),
                        paramsInit.getAnnotationRefresher(),
                        Optional.of(params.getSaveMonitor()));

        PanelNavigation panelNavigation =
                HelperPanelBuilder.createPanelNavigation(
                        currentStateDisplayer, paramsInit, params, controllers, saveActions);

        configureFromInitAnnotation(
                paramsInit.getInitAnnotation(), currentStateDisplayer, panelNavigation);

        return panelNavigation;
    }

    private static ISaveActionListenerFactory createSaveActions(
            Path annotationPath,
            IQueryAcceptedRejected queryAcceptReject,
            AnnotationRefresher annotationRefresher,
            Optional<SaveMonitor> saveMonitor) {
        return new SaveActionListenerFactory<>(
                new SaveAnnotationMPP(annotationPath),
                queryAcceptReject,
                annotationRefresher,
                saveMonitor);
    }

    private static void configureFromInitAnnotation(
            InitAnnotation annotation,
            CurrentStateDisplayer currentStateDisplayer,
            PanelNavigation panelNavigation) {
        if (annotation.getInitCfg() != null) {
            currentStateDisplayer.init(annotation.getInitCfg());
        }

        if (!annotation.getInitMsg().isEmpty()) {
            panelNavigation.setErrorLabelText(annotation.getInitMsg());
        }
    }
}
