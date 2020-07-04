package org.anchoranalysis.gui.annotation.strategy.builder.mark.panel;

import java.util.Optional;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class HelperPanelBuilder {

	public static PanelNavigation createPanelNavigation(
		CurrentStateDisplayer currentStateDisplayer,
		InitParamsProposeMarks paramsInit,
		AnnotationPanelParams params,
		AnnotationFrameControllers controllers,
		ISaveActionListenerFactory saveActions
	) {
		PanelAnnotationIO panelSave = new PanelAnnotationIO();
		panelSave.addActionsToSavePanel(
			saveActions,
			controllers.action().frame().getFrame()
		);
		
		PanelTool panelTool = createPanelTool(
			currentStateDisplayer,
			paramsInit.getDimensionsViewer(),
			paramsInit.getMarkAnnotator(),
			params.getErrorReporter()
		);
		
		PanelMark panelMark = new PanelMark(
			currentStateDisplayer.confirmReset(),
			currentStateDisplayer.undoRedo(),
			panelTool
		);
		
		PanelNavigation panelNavigation = new PanelNavigation(
			panelTool.getPanel(),
			panelMark,
			panelSave.getPanel()
		);
		
		controllers.popup().addAdditionalMenu(
			panelTool.createSwitchToolMenu()
		);
		
		HelperAddActions.apply(
			controllers.action(),				
			controllers.extractOverlays(),
			params,
			panelTool,
			panelMark
		);
				
		return panelNavigation;
	}
	
	private static PanelTool createPanelTool(
		CurrentStateDisplayer currentStateDisplayer,
		ImageDimensions dimViewer,
		MarkAnnotator markAnnotator,
		ToolErrorReporter errorReporter
	) {
		Optional<EvaluatorWithContext> guessEvaluator = markAnnotator.createGuessEvaluator(errorReporter);
		
		Optional<EvaluatorWithContext> evaluatorSelectPoints = markAnnotator.createSelectPointsEvaluator(
			dimViewer,
			errorReporter
		);
				
		return new PanelTool(
			currentStateDisplayer,
			guessEvaluator,
			evaluatorSelectPoints,
			markAnnotator.getPointsFitterSelectPoints(),
			errorReporter
		);		
	}

}
