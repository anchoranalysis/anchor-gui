package org.anchoranalysis.gui.feature.evaluator.params;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.ResultsVector;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.init.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.SimpleSession;
import org.anchoranalysis.feature.shared.SharedFeatureSet;

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


import org.anchoranalysis.gui.feature.FeatureListWithRegionMap;
import org.anchoranalysis.gui.feature.FeatureWithRegionMap;

/**
 * A session which calculates new parameters for every
 *   feature using a FeatureCalcParamsFactory.
 *   
 *   The features are not initialized
 * 
 * @author Owen Feehan
 *
 */
public class FeatureCalcParamsFactorySession<T extends FeatureCalcParams> extends FeatureSession {

	private FeatureListWithRegionMap<T> featureList;
	private SimpleSession delegate;
	private LogErrorReporter logger;
	
	public FeatureCalcParamsFactorySession( FeatureListWithRegionMap<T> featureList, LogErrorReporter logger ) {
		//delegate = new SequentialSession( featureList.createFeatureList() );
		this.delegate = new SimpleSession();
		this.featureList = featureList;
		this.logger = logger;
	}
	
	public void start() {
		
	}
	
	public ResultsVector calc( Mark mark, NRGStackWithParams raster ) {
		ResultsVector rv = new ResultsVector( featureList.size() );
		
		FeatureInitParams paramsInit = new FeatureInitParams();
		SharedFeatureSet<T> sharedFeatures = new SharedFeatureSet<>();
		
		// Now we calculate all the features one at a time
		int featureIndex = 0;
		for( FeatureWithRegionMap<T> feature : featureList ) {
		
			double featVal;
			try {
				// NOTE
				// No InitParams for now
				featVal = calcFeature( feature.getFeature(), raster, mark, feature.getRegionMap(), paramsInit, sharedFeatures );
				rv.set(featureIndex, featVal);
			} catch (FeatureCalcException | InitException e) {
				logger.getErrorReporter().recordError(FeatureList.class, e);
				rv.setError(featureIndex, e);
			}
						
			featureIndex++;
		}
		
		return rv;
	}
	
	private double calcFeature( Feature<T> feature, NRGStackWithParams raster, Mark mark, RegionMap regionMap, FeatureInitParams initParams, SharedFeatureSet sharedFeatures ) throws FeatureCalcException, InitException {
		
		PxlMarkMemo pmm = PxlMarkMemoFactory.create( mark, raster.getNrgStack(), regionMap );
		
		FeatureCalcParamsFactory paramsFactory = ParamsFactoryForFeature.factoryFor( feature );
		
		// Depending on what type of feature it is, we create different types of paramters
		FeatureCalcParams featureCalcParams;
		try {
			featureCalcParams = paramsFactory.create(pmm, raster);
		} catch (CreateException e) {
			throw new FeatureCalcException(e);
		}
		
		double d = delegate.calc( feature, initParams, sharedFeatures, featureCalcParams, logger );
		assert( !Double.isNaN(d) );
		 
		return d;
	}


}
