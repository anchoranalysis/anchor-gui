package org.anchoranalysis.gui.feature.evaluator.nrgtree;

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


import javax.swing.tree.TreeNode;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;


class CustomRootNode extends FeatureListNode {
	
	public CustomRootNode( ErrorReporter errorReporter ) {
		super(errorReporter);
		initChildFeatures( new FeatureList<>(), null );
	}
	
	public void replaceFeatureList(
		FeatureList<FeatureCalcParams> featureList,
		ParamsSource params
	) {
		getFeatures().clear();
		resetCalcList();
		this.initChildFeatures(featureList, params);
	}
	
	public void replaceCalcParams( ParamsSource params ) {
		updateValueSource( params );
	}

	@Override
	public String getValue() {
		return "root";
	}

	@Override
	public TreeNode getParent() {
		return null;
	}

	@Override
	public Feature<FeatureCalcParams> getFeature() {
		return null;
	}

	@Override
	public boolean hasError() {
		return false;
	}

	@Override
	public String getErrorText() {
		return null;
	}

	@Override
	public Throwable getError() {
		assert false;
		return null;
	}


}