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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.session.Subsession;

//
//  TODO  close
//
abstract class FeatureListNode extends Node {
	
	/**
	 * The child features of this particular node
	 */
	private FeatureList childFeatures;
	
	
	private List<FeatureValueNode> calcList = null;
	private List<FeatureCalcParams> paramsList;
	private ErrorReporter errorReporter;
	
	private Subsession subsession;
	
	public FeatureListNode( ErrorReporter errorReporter ) {
		this.errorReporter = errorReporter;
	}
	
	protected void initChildFeatures(FeatureList features,List<FeatureCalcParams> paramsList, Subsession subsession ) {
		this.childFeatures = new FeatureList(features);
		
		// Sort out features in alphabetical order
		Collections.sort( childFeatures, (f1,f2)->f1.getFriendlyName().compareTo(f2.getFriendlyName() ));
		
		this.paramsList = paramsList;
		this.subsession = subsession;
	}

	protected void resetCalcList() {
		calcList = null;
	}
	

	protected void updateValueSourceNoTransformParams(FeatureCalcParams params, Subsession subsession) {
		this.subsession = subsession;
		updateValueSourceNoTransformParams( listSize(params, childFeatures.size() ), subsession);
	}

	
	protected void updateValueSourceNoTransformParams(List<FeatureCalcParams> paramsList, Subsession subsession) {
		
		//System.out.println("updateValueSource");
		
		this.paramsList = paramsList;
		this.subsession = subsession;
		assert( paramsList.size()==childFeatures.size() );
		
		if (calcList==null) {
			// We skip the update, if it's never been calculated, as there is no need to update
			//createCalcList();
		} else {
			ResultsVector rv = calcResults();
			updateNodes( rv, childFeatures, paramsList, calcList );
		}
	}

	
	protected static List<FeatureCalcParams> listSize( FeatureCalcParams params, int size ) {
		List<FeatureCalcParams> list = new ArrayList<>();
		for( int i=0; i<size; i++) {
			list.add(params);
		}
		return list;
	}
		
	@Override
	public Enumeration<? extends TreeNode> children() {
		
		System.out.println("Children called");
		if (calcList==null) {
			createCalcList();
		}
		
		return Collections.enumeration(calcList);
	}
	
	private static void setNodeFromResultsVector( FeatureValueNode node, ResultsVector rv, int index ) {
		
		Double dbl = rv.getDoubleOrNull(index);
		
		if (dbl!=null) {
			//assert( !Double.isNaN(dbl) );
			node.setNrgValue( dbl );
		} else {
			Throwable e = rv.getException(index);
			node.setErrorText( e.toString(), e );
		}
	}
	
	// We only calculate the features on the immediate children
	private void createAndAddNodes( ResultsVector rv, List<FeatureValueNode> listNodes, TreeNode parent ) throws CreateException {
		for (int i=0; i<rv.length(); i++) {
			
			Feature f = childFeatures.get(i);
			FeatureCalcParams params = paramsList.get(i);
			
			assert(f!=null);
			
			FeatureValueNode node = new FeatureValueNode( f, parent, params, errorReporter, subsession );
			setNodeFromResultsVector( node, rv, i );
			
			listNodes.add( node );
		}
	}
	
	
	// We update the immediate children, and all their children
	private void updateNodes( ResultsVector rv, FeatureList features, List<FeatureCalcParams> paramsList, List<FeatureValueNode> listNodes ) {
		assert(rv.length()==features.size());
		// update our tree
		for (int i=0; i<features.size(); i++) {
			
			FeatureCalcParams params = paramsList.get(i);			
			
			FeatureValueNode node = listNodes.get(i);
			setNodeFromResultsVector( node, rv, i );
			
			// We make a new list with a single item
			node.updateValueSource( params, subsession );
		}
	}
	
	private ResultsVector calcResults() {
		return subsession.calcSubsetSuppressErrors(childFeatures,paramsList, errorReporter);
	}
		
	private void createCalcList() {
		try {
			calcList = new ArrayList<>();
			
			assert( paramsList.size()==childFeatures.size() );
	
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

	protected FeatureList getFeatures() {
		return childFeatures;
	}

	protected ErrorReporter getErrorReporter() {
		return errorReporter;
	}
}