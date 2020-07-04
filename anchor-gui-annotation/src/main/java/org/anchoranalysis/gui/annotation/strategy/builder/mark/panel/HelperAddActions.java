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

import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.gui.frame.cfgproposer.CfgProposerMouseClickAdapter;
import org.anchoranalysis.gui.frame.details.canvas.ControllerAction;
import org.anchoranalysis.gui.frame.details.canvas.ControllerMouse;
import org.anchoranalysis.gui.frame.overlays.IExtractOverlays;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelMark;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelTool;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class HelperAddActions {
	
	public static void apply(
		ControllerAction action,
		IExtractOverlays extractOverlays,
		AnnotationPanelParams params,
		PanelTool panelTool,
		PanelMark panelMark
	) {
		addClickListener(
			extractOverlays,
			action.mouse(),
			panelTool,
			params.getSliderState(),
			params.getRandomNumberGenerator(),
			params.getErrorReporter().getErrorReporter()
		);
			
		setupKeystrokesOnInputMap(
			action.keyboard().getInputMap()
		);

		AddConfirmRotate.apply( 
			panelTool,
			panelMark,
			params.getSliderState(),
			action.keyboard(),
			action.mouse()
		);
	}
	

	private static void setupKeystrokesOnInputMap( InputMap inputMap ) {
		inputMap.put( KeyStroke.getKeyStroke("ENTER"), "confirmMark");
		inputMap.put( KeyStroke.getKeyStroke("SPACE"), "confirmMark");
		inputMap.put( KeyStroke.getKeyStroke("INSERT"), "confirmMark");
		inputMap.put( KeyStroke.getKeyStroke('z'), "confirmMark");
		inputMap.put( KeyStroke.getKeyStroke('y'), "confirmMark");
		
//			inputMap.put( KeyStroke.getKeyStroke('d'), "removeMark");
//			inputMap.put( KeyStroke.getKeyStroke("DELETE"), "removeMark");
//			inputMap.put( KeyStroke.getKeyStroke("BACKSPACE"), "removeMark");
		
		inputMap.put( KeyStroke.getKeyStroke("RIGHT"), "rotateToolRight");
		inputMap.put( KeyStroke.getKeyStroke("LEFT"), "rotateToolLeft");
	}
	
	private static void addClickListener(
		IExtractOverlays extractOverlays,
		ControllerMouse controllerMouse,
		PanelTool panelTool,			
		ISliderState sliderState,
		RandomNumberGenerator rng,
		ErrorReporter errorReporter
	) {
		CfgProposerMouseClickAdapter clickListener = new CfgProposerMouseClickAdapter(
			extractOverlays,
			sliderState,
			() -> panelTool.getTool().evaluatorWithContextGetter(),
			rng,
			errorReporter
		);
		clickListener.addCfgProposedListener(
			proposedCfg -> panelTool.getTool().proposed(proposedCfg)
		);
		
		controllerMouse.addMouseListener( clickListener, false);
	}
}
