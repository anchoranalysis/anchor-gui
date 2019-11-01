package org.anchoranalysis.gui.annotation.mark;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.points.fitter.PointsFitter;

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

import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.gui.annotation.AnnotatorModuleCreator;
import org.anchoranalysis.gui.annotation.strategy.MarkProposerStrategy;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorRslvd;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorSetForImage;
import org.anchoranalysis.gui.videostats.internalframe.annotator.tool.ToolErrorReporter;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.EvaluatorWithContext;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.stack.Stack;

public class MarkAnnotator {

	private MarkEvaluatorRslvd markEvaluatorRslvd;
	private MarkProposer markProposerGuess;
	private PointsFitter pointsFitterSelectPoints;

	private NamedProviderStore<Stack> backgroundStacks;
	
	public MarkAnnotator(
		MarkProposerStrategy annotationStrategy,
		MarkEvaluatorSetForImage markEvaluatorSet,
		LogErrorReporter logErrorReporter
	) throws CreateException {
		
		try {
			markEvaluatorRslvd = markEvaluatorSet.get(annotationStrategy.getMarkEvaluatorName());
		} catch (GetOperationFailedException e1) {
			throw new CreateException(e1);
		}
		
		MPPInitParams soMPP;
		try {
			soMPP = markEvaluatorRslvd.getProposerSharedObjectsOperation().doOperation();
			
			pointsFitterSelectPoints = soMPP.getPoints().getPointsFitterSet().getException(annotationStrategy.getPointsFitterName());
			
		} catch (ExecuteException | GetOperationFailedException e) {
			throw new CreateException(e);
		}
			
		try {
			markProposerGuess = soMPP.getMarkProposerSet().getException(annotationStrategy.getMarkProposerName());
		} catch (GetOperationFailedException e) {
			markProposerGuess = null;
			logErrorReporter.getLogReporter().log("Proceeding without 'Guess Tool' as an error occured");
			logErrorReporter.getErrorReporter().recordError(AnnotatorModuleCreator.class, e);
		}
		
		this.backgroundStacks = soMPP.getImage().getStackCollection();
	}
	
	public RegionMap getRegionMap() {
		return markEvaluatorRslvd.getNrgScheme().getRegionMap();
	}

	public NamedProviderStore<Stack> getBackgroundStacks() {
		return backgroundStacks;
	}
	
	public EvaluatorWithContext createGuessEvaluator(ToolErrorReporter errorReporter) {
		return EvaluatorFactory.createGuessEvaluator(
			markProposerGuess,
			markEvaluatorRslvd,
			getRegionMap(),
			errorReporter	
		);
	}
		
	public EvaluatorWithContext createSelectPointsEvaluator(
		ImageDim dimViewer,
		ToolErrorReporter errorReporter
	) {
		return EvaluatorFactory.createSelectPointsEvaluator(
			dimViewer,
			markEvaluatorRslvd,
			getRegionMap(),
			errorReporter
		);
	}

	public PointsFitter getPointsFitterSelectPoints() {
		return pointsFitterSelectPoints;
	}


}
