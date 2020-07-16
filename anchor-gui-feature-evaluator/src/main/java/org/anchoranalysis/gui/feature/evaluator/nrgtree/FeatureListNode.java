/*-
 * #%L
 * anchor-gui-feature-evaluator
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.gui.feature.evaluator.nrgtree;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import javax.swing.tree.TreeNode;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.input.FeatureInput;


//
//  TODO  close
//
abstract class FeatureListNode extends Node {
	
	private FeatureList<FeatureInput> childFeatures;
	
	
	private List<FeatureValueNode> calcList = null;
	private ParamsSource params;
	private ErrorReporter errorReporter;
	
	public FeatureListNode( ErrorReporter errorReporter ) {
		this.errorReporter = errorReporter;
	}
	
	protected void initChildFeatures(FeatureList<FeatureInput> features, ParamsSource params ) {
		// Sort out features in alphabetical order
		this.childFeatures = features.sort(
			(f1,f2)->f1.getFriendlyName().compareTo(
				f2.getFriendlyName()
			)	
		);
		this.params = params;
	}

	protected void resetCalcList() {
		calcList = null;
	}
	

	@Override
	protected void updateValueSource(ParamsSource params) {
		
		this.params = params;
		
		if (calcList==null) {
			// We skip the update, if it's never been calculated, as there is no need to update
			//createCalcList();
		} else {
			ResultsVector rv = calcResults();
			updateNodes( rv, childFeatures, calcList );
		}
	}
		
	@Override
	public Enumeration<? extends TreeNode> children() {
		
		if (calcList==null) {
			createCalcList();
		}
		
		return Collections.enumeration(calcList);
	}
	
	private static void setNodeFromResultsVector( FeatureValueNode node, ResultsVector rv, int index ) {
		
		Optional<Double> dbl = rv.getDoubleOrNull(index);
		
		if (dbl.isPresent()) {
			node.setNrgValue( dbl.get() );
		} else {
			Throwable e = rv.getException(index);
			node.setErrorText( e.toString(), e );
		}
	}
	
	// We only calculate the features on the immediate children
	private void createAndAddNodes( ResultsVector rv, List<FeatureValueNode> listNodes, TreeNode parent ) throws CreateException {
		for (int i=0; i<rv.length(); i++) {
			
			Feature<FeatureInput> f = childFeatures.get(i);
			assert(f!=null);
			
			FeatureValueNode node = new FeatureValueNode(
				f,
				parent,
				params,
				errorReporter
			);
			setNodeFromResultsVector( node, rv, i );
			
			listNodes.add( node );
		}
	}
	
	
	// We update the immediate children, and all their children
	private void updateNodes(
		ResultsVector rv,
		FeatureList<FeatureInput> features,
		List<FeatureValueNode> listNodes
	) {
		assert(rv.length()==features.size());
		// update our tree
		for (int i=0; i<features.size(); i++) {
			
			FeatureValueNode node = listNodes.get(i);
			setNodeFromResultsVector( node, rv, i );
			
			// We make a new list with a single item
			node.updateValueSource( params );
		}
	}
	
	private ResultsVector calcResults() {
		return calcSubsetSuppressErrors(childFeatures,params, errorReporter);
	}
		
	private void createCalcList() {
		try {
			calcList = new ArrayList<>();
	
			ResultsVector rv = calcResults();
			createAndAddNodes( rv, calcList, this );
			
		} catch (CreateException e) {
			errorReporter.recordError( FeatureListNode.class, e );
		}
	}

	@Override
	public boolean getAllowsChildren() {
		return childFeatures.size()>0;
	}

	@Override
	public TreeNode getChildAt(int arg0) {
		
		if (calcList==null) {
			createCalcList();
		}
		
		assert (arg0 < calcList.size());
		return calcList.get(arg0);
	}

	@Override
	public int getChildCount() {
		return childFeatures.size();
	}

	@Override
	public int getIndex(TreeNode arg0) {
		return calcList.indexOf(arg0);
	}

	@Override
	public boolean isLeaf() {
		return childFeatures.size()==0;
	}

	protected FeatureList<FeatureInput> getFeatures() {
		return childFeatures;
	}

	protected ErrorReporter getErrorReporter() {
		return errorReporter;
	}
	
	
	/** Calculates with different parameters for every feature. No cache invalidation is occuring here. TODO Fix */
	private static ResultsVector calcSubsetSuppressErrors(
		FeatureList<FeatureInput> features,
		ParamsSource params,
		ErrorReporter errorReporter
	) {
		ResultsVector res = new ResultsVector( features.size() );

		for( int i=0; i<features.size(); i++) {
			Feature<FeatureInput> f = features.get(i);
			
			try {
				res.set(
					i,
					params.calc(f)
				);
			} catch (Exception e) {
				res.setError(i,e);
				errorReporter.recordError(FeatureListNode.class, e);
			}
			
		}
		return res;
	}
}
