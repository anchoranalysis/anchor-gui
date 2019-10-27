package org.anchoranalysis.gui.annotation.strategy.builder.mark;

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

import java.io.IOException;
import java.nio.file.Path;

import org.anchoranalysis.annotation.io.input.AnnotationWithStrategy;
import org.anchoranalysis.annotation.io.mark.MarkAnnotationReader;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
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
import org.anchoranalysis.gui.annotation.strategy.GenerathorPathRslvr;
import org.anchoranalysis.gui.annotation.strategy.MarkProposerStrategy;
import org.anchoranalysis.gui.annotation.strategy.builder.mark.panel.CreateNavigationPanel;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationFrameControllers;
import org.anchoranalysis.gui.videostats.internalframe.annotator.AnnotationPanelParams;
import org.anchoranalysis.gui.videostats.internalframe.annotator.navigation.PanelNavigation;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;

public class BuilderProposeMarks extends AnnotationGuiBuilderWithDelegate<InitParamsProposeMarks,MarkProposerStrategy> {

	private MarkAnnotationReader annotationReader = new MarkAnnotationReader(true);
		
	private OpenAnnotationMPP openAnnotation;

	public BuilderProposeMarks(AnnotationWithStrategy<MarkProposerStrategy> delegate) throws IOException {
		super(delegate);
		this.openAnnotation = createOpenAnnotation();
	}
	
	private OpenAnnotationMPP createOpenAnnotation() throws IOException {
		Path cfgPath = new GenerathorPathRslvr(pathForBinding()).pathOrNull(
			getStrategy().getDefaultCfgFilePathGenerator()
		); 
		return new OpenAnnotationMPP(
			annotationPath(),
			cfgPath,
			annotationReader
		);
	}

	@Override
	public InitParamsProposeMarks createInitParams(
		ProgressReporterMultiple prm,
		AnnotationGuiContext context,
		LogErrorReporter logErrorReporter,
		boolean useDefaultCfg
	) throws CreateException {
		
		try {
			MarkAnnotator markAnnotator = CreateMarkEvaluator.apply(
				context.getMarkEvaluatorManager(),
				pathForBinding(),
				getStrategy(),
				stacks(),
				logErrorReporter
			);
			
			// Open initial cfg
			InitAnnotation annotationExst = openAnnotation.open(
				useDefaultCfg,
				logErrorReporter
			);
		
			return new InitParamsProposeMarks(
				context.getAnnotationRefresher(),
				markAnnotator,
				createBackground(prm, markAnnotator.getBackgroundStacks() ),
				annotationExst
			);
			
		} catch (VideoStatsModuleCreateException e) {
			throw new CreateException(e);
		}
	}
	

	/** Creates a PanelNavigation to support the annotation */
	@Override
	public PanelNavigation createInitPanelNavigation(
		InitParamsProposeMarks paramsInit,
		AnnotationPanelParams params,
		AnnotationFrameControllers controllers
	) {
		return CreateNavigationPanel.apply(
			paramsInit,
			params,
			controllers,
			annotationPath()
		);
	}
	
	@Override
	public void showAllAdditional(
		InitParamsProposeMarks paramsInit,
		AdditionalFramesContext context
	) throws OperationFailedException {
		
		ShowAdditionalFrames.apply(
			paramsInit,
			context,
			pathForBinding(),
			getStrategy()
		);
		
	}
	
	/** Provides for an interactive GUI export of an annotation */
	@Override
	public ExportAnnotation exportAnnotation() {
		return new ExportScaledMarks( annotationPath() );
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
		return CreateAnnotationSummary.apply(
			annotationPath(),
			annotationReader
		);
	}
}
