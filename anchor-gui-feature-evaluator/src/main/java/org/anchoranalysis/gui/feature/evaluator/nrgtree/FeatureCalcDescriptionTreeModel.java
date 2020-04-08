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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.CreateParams;
import org.anchoranalysis.feature.session.SequentialSession;
import org.anchoranalysis.feature.session.SessionUtilities;
import org.anchoranalysis.feature.shared.SharedFeatureSet;
import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.evaluator.nrgtree.overlayparams.CreateParamsFromOverlay;
import org.anchoranalysis.gui.feature.evaluator.singlepair.IUpdatableSinglePair;

public class FeatureCalcDescriptionTreeModel extends DefaultTreeModel implements IUpdatableSinglePair {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5795973516009041187L;

	private FeatureListWithRegionMap<FeatureCalcParams> featureListWithRegions;
	private FeatureList<FeatureCalcParams> featureList;
	
	private LogErrorReporter logErrorReporter;
	private SharedFeatureSet<FeatureCalcParams> sharedFeatures;
	
	private SequentialSession<FeatureCalcParams> session = null;
	
	//private static Log log = LogFactory.getLog(FeatureCalcDescriptionTreeModel.class);
	
	private boolean first = true;
	
	public FeatureCalcDescriptionTreeModel(
		FeatureListWithRegionMap<FeatureCalcParams> featureListWithRegions,
		SharedFeatureSet<FeatureCalcParams> sharedFeatures,
		LogErrorReporter logErrorReporter
	) {
		super( null );

		this.featureListWithRegions = featureListWithRegions;
		
		// We sort both feature lists in alphabetical order
		this.featureListWithRegions.sortAlphaAscend();
		
		this.featureList = featureListWithRegions.createFeatureList();
		
		
		
		final TreeNode root = new CustomRootNode(logErrorReporter.getErrorReporter());
		setRoot(root);
		
		this.logErrorReporter = logErrorReporter;
		
		this.sharedFeatures = removeFeaturesFromShared(sharedFeatures, featureList);
	}

	private FeatureInitParams createInitParams( NRGStackWithParams nrgStack ) {
		FeatureInitParams params = new FeatureInitParams( nrgStack.getParams() );
		params.setNrgStack(nrgStack.getNrgStack());
		return params;
	}
	

	@Override
	public void updateSingle(Overlay overlay, NRGStackWithParams nrgStack) {
		
		try {
			
			// If we have no mark matching the current id
			if (overlay==null || featureListWithRegions.size()==0) {
				return;
			}
			
			List<CreateParams<FeatureCalcParams>> createParamsList = new ArrayList<>();
			CreateParamsFromOverlay.addForOverlay( overlay, nrgStack, featureListWithRegions, createParamsList );
			
			updateOrReload(
				featureList,
				createParamsList,
				nrgStack
			);

		} catch (OperationFailedException e) {
			logErrorReporter.getErrorReporter().recordError( FeatureCalcDescriptionTreeModel.class, e);
		}
	}
	
	
	
	@Override
	public void updatePair(Pair<Overlay> pair, NRGStackWithParams nrgStack) {
		
		try {
			
			// If we have no mark matching the current id
			if (pair==null || featureListWithRegions.size()==0) {
				return;
			}
			
			// Create a pair for each region map
			List<CreateParams<FeatureCalcParams>> createParamsList = new ArrayList<>();
			
			CreateParamsFromOverlay.addForOverlayPair( pair, nrgStack, featureListWithRegions, createParamsList );
			
			updateOrReload(
				featureList,
				createParamsList,
				nrgStack
			);
		
		} catch (OperationFailedException e) {
			logErrorReporter.getErrorReporter().recordError( FeatureCalcDescriptionTreeModel.class, e);
		}
	}
	
	private List<FeatureCalcParams> createParamsList( List<CreateParams<FeatureCalcParams>> listCreate, FeatureList<?> features ) throws CreateException {
		assert( listCreate.size()==features.size() );
		
		FeatureCalcParams paramsGlobal=null;
		
		List<FeatureCalcParams> listOut = new ArrayList<>();
		
		for( int i=0; i<features.size(); i++) {
			CreateParams<FeatureCalcParams> cp = listCreate.get(i);
			Feature<?> f = features.get(i);
			
			FeatureCalcParams params = cp.createForFeature(f);
			
			if (paramsGlobal==null) {
				paramsGlobal=params;
			}
			
			// OWENF: COMMENTED OUT AS IT WAS BEING VIOLATED, BUT AM SURE WHAT'S GOING ON
			/*if (params!=paramsGlobal) {
				assert false;
			}*/
			
			listOut.add( params );
		}
		
		return listOut;
	}
	
	
	private void updateOrReload( FeatureList<FeatureCalcParams> featureList, List<CreateParams<FeatureCalcParams>> createParamsList, NRGStackWithParams nrgStack ) throws OperationFailedException {
		
		try {
			// Create a list of params for each feature
			List<FeatureCalcParams> paramsList = createParamsList(createParamsList, featureList);
			
			CustomRootNode root = (CustomRootNode) getRoot();
			//root.setFeatureCalcParams(featureCalcParams);
	
			FeatureInitParams paramsInit = createInitParams(nrgStack); 
			if (first) {
				// Later on, both the features in featureList and various dependent features will be called through Subsessions
				//   so we rely on these dependent features being initialised through session.start() as the initialization
				//   procedure is recursive
				
				
				
				session = new SequentialSession<>(this.featureList);
				session.start( paramsInit, sharedFeatures, logErrorReporter );
				
				//log.info( String.format("first") );
				assert( createParamsList.size()==featureList.size() );
				
				root.replaceFeatureList(
					featureList,
					SessionUtilities.createCacheable(
						paramsList,
						featureList,
						sharedFeatures,
						paramsInit,
						logErrorReporter
					),
					session.createSubsession()
				);
				
				nodeStructureChanged(root);
				reload();
				first = false;
				
			} else {
				//log.info( String.format("updateValueSource") );
				assert( createParamsList.size()==featureList.size() );
				
				root.replaceCalcParams(
					SessionUtilities.createCacheable(
						paramsList,
						featureList,
						sharedFeatures,
						paramsInit,
						logErrorReporter
					),
					session.createSubsession()
				);

				nodeChanged(root);
			}
			
		} catch (InitException | CreateException | FeatureCalcException e) {
			throw new OperationFailedException(e);
		}
	}

	/** Removes features from shared, which also exist in the FeatureList, as they should
	 *  not be in both */
	private static SharedFeatureSet<FeatureCalcParams> removeFeaturesFromShared(
		SharedFeatureSet<FeatureCalcParams> sharedFeatures,
		FeatureList<FeatureCalcParams> featureList
	) {
		SharedFeatureSet<FeatureCalcParams> dup = sharedFeatures.duplicate();
		dup.removeIfExists(featureList);
		return dup;
	}
}
