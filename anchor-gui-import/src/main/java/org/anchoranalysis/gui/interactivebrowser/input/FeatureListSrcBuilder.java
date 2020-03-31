package org.anchoranalysis.gui.interactivebrowser.input;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGScheme;
import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;

/*-
 * #%L
 * anchor-gui-import
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;
import org.anchoranalysis.gui.feature.evaluator.params.FeatureCalcParamsFactory;
import org.anchoranalysis.gui.feature.evaluator.params.ParamsFactoryForFeature;
import org.anchoranalysis.gui.feature.evaluator.treetable.ExtractFromNamedNRGSchemeSet;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.feature.evaluator.treetable.KeyValueParamsAugmenter;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

public class FeatureListSrcBuilder {

	private LogErrorReporter logErrorReporter;
		
	public FeatureListSrcBuilder(LogErrorReporter logErrorReporter) {
		super();
		this.logErrorReporter = logErrorReporter;
	}

	/**
	 * Builds the nrgSchemeSet
	 * @param soFeature
	 * @param nrgSchemeCreator optional parameter. ignored if NULL
	 * @return
	 * @throws CreateException
	 */
	public FeatureListSrc build( SharedFeaturesInitParams soFeature, NRGSchemeCreator nrgSchemeCreator ) throws CreateException {

		NamedNRGSchemeSet nrgElemSet = new NamedNRGSchemeSet(soFeature.getSharedFeatureSet() );
		
		if (nrgSchemeCreator!=null) {
			return buildWith( soFeature, nrgElemSet, nrgSchemeCreator );
			
		} else {
			return buildWithout( soFeature, nrgElemSet );
		}
	}
	
	/** Build WITHOUT an existing nrgScheme */
	private FeatureListSrc buildWithout(
		SharedFeaturesInitParams soFeature,
		NamedNRGSchemeSet nrgElemSet
	) {
		addFromStore( nrgElemSet, soFeature.getFeatureListSet(), RegionMapSingleton.instance() );
		return new ExtractFromNamedNRGSchemeSet(nrgElemSet);
	}
	
	/** Build WITH an existing nrgScheme */
	private FeatureListSrc buildWith(
		SharedFeaturesInitParams soFeature,
		NamedNRGSchemeSet nrgElemSet,
		NRGSchemeCreator nrgSchemeCreator
	) throws CreateException {
		
		NRGScheme nrgScheme = createNRGScheme( nrgSchemeCreator, soFeature, logErrorReporter );
		RegionMapFinder.addFromNrgScheme( nrgElemSet, nrgScheme );
		
		addFromStore( nrgElemSet, soFeature.getFeatureListSet(), nrgScheme.getRegionMap() );
		
		// We deliberately do not used the SharedFeatures as we wish to keep the Image Features seperate
		//  and prevent any of the features being initialized prematurely
		KeyValueParamsAugmenter augmenter = new KeyValueParamsAugmenter(
			nrgScheme,
			new SharedFeatureSet(),	// soFeature.getSharedFeatureSet(),
			logErrorReporter
		);
		
		return new ExtractFromNamedNRGSchemeSet(nrgElemSet, augmenter );
	}
	
	private static NRGScheme createNRGScheme( NRGSchemeCreator nrgSchemeCreator, SharedFeaturesInitParams soFeature, LogErrorReporter logger ) throws CreateException {
		
		try {
			nrgSchemeCreator.initRecursive( soFeature, logger );
		} catch (InitException e1) {
			throw new CreateException(e1);
		}
		return nrgSchemeCreator.create();
	}
	
	private void addFromStore( NamedNRGSchemeSet nrgElemSet, NamedProviderStore<FeatureList> store, RegionMap regionMap ) {

		// Add each feature-list to the scheme, separating into unary and pairwise terms
		for (String key : store.keys()) {
			try {
				FeatureList fl = store.getException(key);
				
				// Put this in there, to get rid of error. Unsure why. It should go in refactoring when FeatureSessions are properly implemented
				//fl.init( new FeatureInitParams(soFeature.getSharedFeatureSet(), soFeature.getCachedCalculationList()) );
				
				// Determines which features belong in the Unary part of the NRGScheme, and which in the Pairwise part
				FeatureList outUnary = new FeatureList();
				FeatureList outPairwise = new FeatureList();
				determineUnaryPairwiseFeatures( fl, outUnary, outPairwise );
				
				nrgElemSet.add(key, new NRGScheme(outUnary, outPairwise, regionMap ) );
				
			} catch (FeatureCalcException  e) {
				logErrorReporter.getErrorReporter().recordError(FeatureListSrcBuilder.class, e);
			} catch (NamedProviderGetException e) {
				logErrorReporter.getErrorReporter().recordError(FeatureListSrcBuilder.class, e.summarize());
			}
		}
	}
	
	private static void determineUnaryPairwiseFeatures( FeatureList in, FeatureList outUnary, FeatureList outPairwise ) throws FeatureCalcException {
		
		for( Feature f : in ) {
			
			FeatureCalcParamsFactory factory = ParamsFactoryForFeature.factoryFor(f);
			
			if (factory.isUnarySupported()) {
				outUnary.add(f);
			}
			
			if (factory.isPairwiseSupported()) {
				outPairwise.add(f);
			}

		}
	}
}
