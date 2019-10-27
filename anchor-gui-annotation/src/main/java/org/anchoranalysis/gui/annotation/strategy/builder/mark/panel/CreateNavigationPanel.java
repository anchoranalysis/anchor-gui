package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

/*-
 * #%L
 * anchor-gui-annotation
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.nio.file.Path;

import org.anchoranalysis.gui.annotation.InitAnnotation;
import org.anchoranalysis.gui.annotation.AnnotationRefresher;
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
		Path annotationPath
	) {
		CurrentStateDisplayer currentStateDisplayer = new CurrentStateDisplayer(
			new ShowCurrentState(
				controllers.showOverlays(),
				params.getErrorReporter()
			),
			params.getSaveMonitor(),
			paramsInit.getDimensionsViewer(),
			paramsInit.getMarkAnnotator().getRegionMap(),	// How we calculate the overlap
			params.getErrorReporter()
		);
		
		ISaveActionListenerFactory saveActions = createSaveActions(
			annotationPath,
			currentStateDisplayer.queryAcceptReject(),
			paramsInit.getAnnotationRefresher(),
			params.getSaveMonitor()
		);
	
		PanelNavigation panelNavigation = HelperPanelBuilder.createPanelNavigation(
			currentStateDisplayer,
			paramsInit,
			params,
			controllers,
			saveActions
		);
		
		configureFromInitAnnotation(
			paramsInit.getInitAnnotation(),
			currentStateDisplayer,
			panelNavigation
		);
		
		return panelNavigation;
	}
	
	private static ISaveActionListenerFactory createSaveActions(
		Path annotationPath,
		IQueryAcceptedRejected queryAcceptReject,
		AnnotationRefresher annotationRefresher,
		SaveMonitor saveMonitor
	) {
		return new SaveActionListenerFactory<>(
			new SaveAnnotationMPP(annotationPath),
			queryAcceptReject,
			annotationRefresher,
			saveMonitor
		);
	}
	
	private static void configureFromInitAnnotation(
		InitAnnotation annotation,
		CurrentStateDisplayer currentStateDisplayer,
		PanelNavigation panelNavigation
	) {
		if (annotation.getInitCfg()!=null) {
			currentStateDisplayer.init(annotation.getInitCfg());
		}
				
		if (!annotation.getInitMsg().isEmpty()) {
			panelNavigation.setErrorLabelText(annotation.getInitMsg());
		}
	}
}
