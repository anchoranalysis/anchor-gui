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

import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.params.FeatureInput;


// FeatureValue
class FeatureValueNode extends FeatureListNode {

	// The current value defining the feature (to be displayed to the user)
	private String value;
	
	private Feature<FeatureInput> parentFeature;
	private String errorText = "";
	private Throwable error;
	
	private TreeNode parentNode;
	
	private FeatureList<FeatureInput> childFeatures;
	
	public FeatureValueNode(
		Feature<FeatureInput> parentFeature,
		TreeNode parentNode,
		ParamsSource params,
		ErrorReporter errorReporter
	) throws CreateException {
		super(errorReporter);
	
		this.parentNode = parentNode;
		this.parentFeature = parentFeature;
		
		try {
			childFeatures = parentFeature.createListChildFeatures(true);
		} catch (BeanMisconfiguredException e) {
			errorReporter.recordError(FeatureValueNode.class, e);
			childFeatures = new FeatureList<>();
		}
		
		// For the children, we embed the createParams that is used for the parent
		//  inside another CreateParams that optionally transforms the params for the child
		//CreateParams createParamsChild = new CreateParamsTransformChild( param, feature );
		
		initChildFeatures(
			childFeatures,
			createChildParam(parentFeature,params,childFeatures)
		);
	}
	
	private static ParamsSource createChildParam(
		Feature<FeatureInput> parentFeature,
		ParamsSource parentParams,
		FeatureList<FeatureInput> childFeatures
	) throws CreateException {
		//try {
			//return parentFeature.transformParams(parentParams, childFeature);
	//	} catch (FeatureCalcException e) {
//			throw new CreateException(e);
		//
		return parentParams;
	}
	
	

	@Override
	protected void updateValueSource(ParamsSource paramsSource) {
		try {
			ParamsSource childParams = createChildParam(
				parentFeature,
				paramsSource,
				childFeatures
			);
			super.updateValueSource(childParams);
		} catch (CreateException e) {
			getErrorReporter().recordError(FeatureValueNode.class, e);
		}
	}
	
	@Override
	public Feature<FeatureInput> getFeature() {
		return parentFeature;
	}

	@Override
	public TreeNode getParent() {
		return parentNode;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setNrgValue(double nrgValue) {
		this.value = Double.toString(nrgValue);
	}
	
	public void setErrorText( String errorDescription, Throwable cause ) {
		//String seperator = System.getProperty("line.separator");
		this.value = "Error";
		errorText = errorDescription;
		error = cause;
	}
	
	@Override
	public boolean hasError() {
		return errorText!=null && !errorText.isEmpty();
	}
	
	@Override
	public String getErrorText() {
		return errorText;
	}

	@Override
	public Throwable getError() {
		return error;
	}



}