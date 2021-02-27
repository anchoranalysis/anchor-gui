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

package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.annotation.mark.DualMarks;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.InitializationProposeMarks;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.ISaveActionListenerFactory;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveActionListenerFactory;
import org.anchoranalysis.gui.videostats.internalframe.annotator.SaveMonitor;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.CurrentStateDisplayer;
import org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate.ShowCurrentState;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateNavigationPanel {

    public static PanelNavigation apply(
            InitializationProposeMarks initialization,
            AnnotationPanelParams params,
            AnnotationFrameControllers controllers,
            Path annotationPath) {
        CurrentStateDisplayer currentStateDisplayer =
                new CurrentStateDisplayer(
                        new ShowCurrentState(controllers.showOverlays(), params.getErrorReporter()),
                        params.getSaveMonitor(),
                        initialization.dimensionsViewer(),
                        initialization
                                .getMarkAnnotator()
                                .getRegionMap(), // How we calculate the overlap
                        params.getErrorReporter());

        ISaveActionListenerFactory saveActions =
                createSaveActions(
                        annotationPath,
                        currentStateDisplayer.queryAcceptReject(),
                        initialization.getAnnotationRefresher(),
                        Optional.of(params.getSaveMonitor()));

        PanelNavigation panelNavigation =
                HelperPanelBuilder.createPanelNavigation(
                        currentStateDisplayer, initialization, params, controllers, saveActions);

        configureFromInitAnnotation(
                initialization.getInitAnnotation(), currentStateDisplayer, panelNavigation);

        return panelNavigation;
    }

    private static ISaveActionListenerFactory createSaveActions(
            Path annotationPath,
            DualMarks queryAcceptReject,
            AnnotationRefresher annotationRefresher,
            Optional<SaveMonitor> saveMonitor) {
        return new SaveActionListenerFactory(
                new SaveAnnotationMPP(annotationPath),
                queryAcceptReject,
                annotationRefresher,
                saveMonitor);
    }

    private static void configureFromInitAnnotation(
            InitAnnotation annotation,
            CurrentStateDisplayer currentStateDisplayer,
            PanelNavigation panelNavigation) {
        if (annotation.getInitMarks() != null) {
            currentStateDisplayer.init(annotation.getInitMarks());
        }

        if (!annotation.getInitMsg().isEmpty()) {
            panelNavigation.setErrorLabelText(annotation.getInitMsg());
        }
    }
}
