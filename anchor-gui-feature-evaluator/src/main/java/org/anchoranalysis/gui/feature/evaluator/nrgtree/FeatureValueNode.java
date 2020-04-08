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


import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.TreeNode;

import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemAllCalcParams;
import org.anchoranalysis.anchor.mpp.feature.nrg.elem.NRGElemPairCalcParams;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.cache.CacheableParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.Subsession;

// FeatureValue
class FeatureValueNode extends FeatureListNode {

	// The current value defining the feature (to be displayed to the user)
	private String value;
	
	private Feature<FeatureCalcParams> parentFeature;
	private String errorText = "";
	private Throwable error;
	
	private TreeNode parentNode;
	
	private FeatureList<FeatureCalcParams> childFeatures;
	
	public FeatureValueNode(Feature<FeatureCalcParams> parentFeature, TreeNode parentNode, CacheableParams<FeatureCalcParams> params, ErrorReporter errorReporter, Subsession subsession ) throws CreateException {
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
			createAllChildParams(parentFeature,params,childFeatures),
			subsession
		);
	}
	
	private static CacheableParams<FeatureCalcParams> createChildParam( Feature<FeatureCalcParams> parentFeature, CacheableParams<FeatureCalcParams> parentParams, Feature<FeatureCalcParams> childFeature ) throws CreateException {
		try {
			return parentFeature.transformParams(parentParams, childFeature);
		} catch (FeatureCalcException e) {
			throw new CreateException(e);
		}
	}
	
	private static List<CacheableParams<FeatureCalcParams>> createAllChildParams( Feature<FeatureCalcParams> parentFeature, CacheableParams<FeatureCalcParams> parentParams, FeatureList<FeatureCalcParams> childFeatures ) throws CreateException {
		
		List<CacheableParams<FeatureCalcParams>> list = new ArrayList<>();
		
		for( Feature<FeatureCalcParams> f : childFeatures ) {
			list.add(
				createChildParam(parentFeature, parentParams, f)
			);
		}
		
		return list;
	}
	
	// TODO merge with createAllChildParams, very similar names, what's the necessary difference/
	private static List<CacheableParams<FeatureCalcParams>> creatAllChildParams( Feature<FeatureCalcParams> parentFeature, List<CacheableParams<FeatureCalcParams>> parentParams, FeatureList<FeatureCalcParams> childFeatures ) throws CreateException {
		
		assert(parentParams.size()==childFeatures.size());
		
		List<CacheableParams<FeatureCalcParams>> list = new ArrayList<>();
		
		for( int i=0; i<childFeatures.size(); i++ ) {
			Feature<FeatureCalcParams> f = childFeatures.get(i);
			CacheableParams<FeatureCalcParams> params = parentParams.get(i);
			
			list.add(
				createChildParam(parentFeature, params, f)
			);
		}
		
		return list;
	}

	@Override
	protected void updateValueSource(CacheableParams<FeatureCalcParams> params, Subsession subsession) {
		try {
			CacheableParams<FeatureCalcParams> childParams = createChildParam(
				parentFeature,
				params,
				null
			);
			super.updateValueSourceNoTransformParams( childParams, subsession);
		} catch (CreateException e) {
			getErrorReporter().recordError(FeatureValueNode.class, e);
		}
	}

	@Override
	protected void updateValueSource(List<CacheableParams<FeatureCalcParams>> createParamsList, Subsession subsession) {
		try {
			List<CacheableParams<FeatureCalcParams>> childParams = creatAllChildParams(parentFeature, createParamsList, childFeatures);
			super.updateValueSourceNoTransformParams(childParams, subsession);
		} catch (CreateException e) {
			getErrorReporter().recordError(FeatureValueNode.class, e);
		}
		
	}
	
	@Override
	public Feature getFeature() {
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