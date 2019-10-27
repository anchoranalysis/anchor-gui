package org.anchoranalysis.gui.videostats.internalframe.annotator;




/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import javax.swing.JFrame;

import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.gui.annotation.builder.AnnotationGuiBuilder;
import org.anchoranalysis.gui.frame.details.canvas.ControllerFrame;
import org.anchoranalysis.gui.frame.details.canvas.controller.imageview.ControllerImageView;
import org.anchoranalysis.gui.frame.overlays.InternalFrameOverlaysRedraw;
import org.anchoranalysis.gui.image.frame.canvas.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.ControllerPopupMenuWithBackground;
import org.anchoranalysis.gui.videostats.IModuleCreatorDefaultStateSliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.io.bean.output.OutputWriteSettings;

public class InternalFrameAnnotator {

	private InternalFrameOverlaysRedraw delegate;
	
	public InternalFrameAnnotator( String name, ErrorReporter errorReporter ) {
		delegate = new InternalFrameOverlaysRedraw(name);
		delegate.controllerAction().frame().setUseSplitPlane(false);
	}
	
	public <T extends AnnotationInitParams> ISliderState init(
		AnnotationGuiBuilder<T> annotation,
		T paramsInit,
		DefaultModuleState defaultState,
		OperationWithProgressReporter<IAddVideoStatsModule> adder,
		OutputWriteSettings outputWriteSettings,
		VideoStatsModuleGlobalParams mpg
	) throws InitException {
		
		// Configure initial-size based upon overall window size
		delegate.controllerImageView().configureForImage(
			0.8,
			0.6,
			0,
			annotation.heightNonImagePanel(),
			mpg.getGraphicsCurrentScreen(),
			paramsInit.getBackground().getDimensionsViewer()
		);
		
		ISliderState sliderState = delegate.init(
			defaultState,
			adder,
			paramsInit.getBackgroundSetOp(),
			outputWriteSettings,
			mpg
		);

		SaveMonitor saveMonitor = new SaveMonitor();
		
		// Panel navigation and current-state-displayer
		PanelNavigation panelNavigation = createSetupNavigationPanel(
			annotation,
			paramsInit,
			saveMonitor,
			sliderState,
			mpg
		);

		delegate.controllerAction().order().setAsBottomComponent( panelNavigation.getPanel() );
				
		setDelegateAttributes(
			saveMonitor,
			delegate.controllerAction().frame(),
			delegate.controllerImageView()
		);
		
		return sliderState;
	}
		
	public IModuleCreatorDefaultStateSliderState moduleCreator() {
		return delegate.moduleCreator();
	}

	public ControllerPopupMenuWithBackground controllerBackgroundMenu() {
		return delegate.controllerBackgroundMenu();
	}
	
	private <T extends AnnotationInitParams> PanelNavigation createSetupNavigationPanel(
		AnnotationGuiBuilder<T> annotation,
		T paramsInit,
		SaveMonitor saveMonitor,
		ISliderState sliderState,
		VideoStatsModuleGlobalParams mpg
	) {
		ShowError showError = new ShowError();
		ToolErrorReporter toolErrorReporter = new ToolErrorReporter(
			showError,
			mpg.getLogErrorReporter().getErrorReporter()
		);
		
		AnnotationPanelParams params = new AnnotationPanelParams(
			saveMonitor,
			mpg.getRandomNumberGenerator(),
			sliderState,
			toolErrorReporter
		);
		
		PanelNavigation panelNavigation = annotation.createInitPanelNavigation(
			paramsInit,
			params,
			createControllers(sliderState)
		);
		
		showError.setPanelNavigation(panelNavigation);
					
		delegate.controllerBackgroundMenu().setRetrieveElementsInPopupEnabled(false); // Disable export-menu in popup
		
		return panelNavigation;
	}
	
	private AnnotationFrameControllers createControllers(ISliderState sliderState) {
		return new AnnotationFrameControllers(
			delegate.extractOverlays(),
			delegate.showOverlays(sliderState),
			delegate.controllerBackgroundMenu(),
			delegate.controllerAction()
		);
	}
					
	private static void setDelegateAttributes( SaveMonitor saveMonitor, ControllerFrame controllerFrame, ControllerImageView controllerImageView ) {

		// We disallow existing
		controllerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		controllerFrame.addInternalFrameListener( new FrameClosedListener(saveMonitor) );
		
		controllerImageView.setEnforceMinimumSizeAfterGuessZoom(false);
	}


}
