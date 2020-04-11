package org.anchoranalysis.gui.feature.evaluator;

/*-
 * #%L
 * anchor-gui-feature-evaluator
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

import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.FeatureCalcDescriptionTreeRenderData;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.FeatureCalcDescriptionTreeRowModel;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.feature.evaluator.treetable.ITreeTableModel;
import org.anchoranalysis.gui.feature.evaluator.treetable.TreeTableProperties;
import org.anchoranalysis.gui.feature.evaluator.treetable.TreeTableWithModelMultiplex;

class UpdatableTreeTableFactory {

	/**
	 * Creates a tree-table model, adds it to a split-pane and returns an Updater that
	 *  can be used to change it, as is needed
	 *  
	 * @param descriptionTopPanel
	 * @param sharedFeatureList
	 * @param firstUpdatableSinglePair
	 * @param nrgElemCollection
	 * @param splitPane where the model is added to
	 * @param logger
	 * @param pmmhFactory
	 * @return
	 */
	public static UpdatableTreeTable create(
		OverlayDescriptionPanel overlayDescriptionPanel,
		FeatureListSrc featureListSrc,
		LogErrorReporter logger
	) {
		
		ITreeTableModel treeTable = createTreeTable(featureListSrc, logger);
    	
		SinglePairUpdater markPairList = new SinglePairUpdater(
			overlayDescriptionPanel,
			new FinderEvaluator(featureListSrc.sharedFeatures().downcast(), logger),
			treeTable
		);
		
		return new UpdatableTreeTable( treeTable, markPairList );
	}
	
	private static ITreeTableModel createTreeTable(	FeatureListSrc featureListSrc, LogErrorReporter logger ) {
		FeatureCalcDescriptionTreeRowModel rowModel = new FeatureCalcDescriptionTreeRowModel();
		
		TreeTableProperties properties = new TreeTableProperties(
			rowModel,
    		new FeatureCalcDescriptionTreeRenderData(logger.getErrorReporter()),
    		"NRG Term",
    		logger
		);
		
		ITreeTableModel treeTable = new TreeTableWithModelMultiplex( properties, featureListSrc );
    	treeTable.addMouseListenerToOutline( new ShowErrorMessageMouseListener(treeTable, logger.getErrorReporter()) );
    	return treeTable;
	}
	
	
	
}
